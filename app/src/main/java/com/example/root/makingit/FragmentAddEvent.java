package com.example.root.makingit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class FragmentAddEvent extends DialogFragment {
    final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    DialogFragment frag = this;
    Button addImage;
    public onActionListener listner;
    interface onActionListener
    {
        void dismissMe(DialogFragment frag);
        void makeSnackB(String msg);
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
        void tellAboutAddition(EventInfo eventInfo);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_add_event,viewGroup, false);
        final EditText ename,edetail;
        listner = (onActionListener) getActivity();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Button enter = view.findViewById(R.id.enter);
        addImage =view.findViewById(R.id.addEventImage);
        ename = view.findViewById(R.id.evname);
        edetail = view.findViewById(R.id.evdetail);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String evname,evdetail;
                evname = ename.getText().toString();
                evdetail = edetail.getText().toString();
                if (TextUtils.isEmpty(evname)) {
                    listner.makeSnackB("Enter Event Name!");
                    return;
                }

                if (TextUtils.isEmpty(evdetail)) {
                    listner.makeSnackB("Enter Event Detail!");
                    return;
                }
                saveEventData(evname,evdetail);
                listner.dismissMe(frag);

            }
        });
        addImage.setOnClickListener(loadImage);
        return view;
    }
    View.OnClickListener loadImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseImage();
        }
    };
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void saveEventData(final String evname,final String evdetail)
    {
        listner.makeLoadingSnackBar("Saving Event...");
        final  FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("events").document();
        if(filePath != null)
        {
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            }
            byte[] data = baos.toByteArray();
            final StorageReference ref = storageReference.child("event_pics/"+ Objects.requireNonNull(docRef.getId()));
            ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             String evauthor;
                             evauthor =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                             final EventInfo event = new EventInfo(docRef.getId(),evname,evdetail,evauthor,uri.toString());
                             docRef.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     listner.dismissSnackBar();
                                     listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));
                                     listner.tellAboutAddition(event);
                                 }
                             });
                         }
                     });
                }
            });
        }
        else {
            String evauthor;
            evauthor = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            EventInfo event = new EventInfo(docRef.getId(), evname, evdetail, evauthor, null);
            docRef.set(event);
            listner.dismissSnackBar();
            listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));
            listner.tellAboutAddition(event);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            if(addImage!=null)
            {
            addImage.setBackground(getResources().getDrawable(R.drawable.ic_check));
            addImage.setEnabled(false);}
        }
    }
}