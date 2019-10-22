package com.example.root.makingit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
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

public class FragmentAddForum extends FragmentAddEvent {
    private onForumAdded mListener;
    final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    FirebaseStorage storage;
    Button addImage;
    StorageReference storageReference;
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    interface onForumAdded{
        void addedForumPost(ForumPostInfo forumPostInfo);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_forum, viewGroup, false);
        final EditText ename,edetail;
        listner = (onActionListener) getActivity();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mListener = (onForumAdded) getActivity();
        Button enter = view.findViewById(R.id.forumEnter);
        addImage =view.findViewById(R.id.addForumImage);
        ename = view.findViewById(R.id.forumName);
        edetail = view.findViewById(R.id.forumDetail);
        addImage.setOnClickListener(loadImage);
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
                saveForumData(evname,evdetail);
                listner.dismissMe(frag);
            }
        });
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
    public void saveForumData(final String evname,final String evdetail)
    {
        listner.makeLoadingSnackBar("Saving Forum...");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("forum_posts").document();
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
            final StorageReference ref = storageReference.child("forum_pics/"+ Objects.requireNonNull(docRef.getId()));
            ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            String evauthor;
                            evauthor =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            final ForumPostInfo forumpost = new ForumPostInfo(docRef.getId(),evname,evdetail,evauthor,"0","0",uri.toString());
                            docRef.set(forumpost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    listner.dismissSnackBar();
                                    listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));
                                    mListener.addedForumPost(forumpost);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).delete();
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
            ForumPostInfo forumpost = new ForumPostInfo(docRef.getId(),evname,evdetail,evauthor,"0","0",null);
            docRef.set(forumpost);
            listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));
            mListener.addedForumPost(forumpost);
        }
    }
}
