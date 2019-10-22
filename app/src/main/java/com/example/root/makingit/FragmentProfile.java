package com.example.root.makingit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends Fragment {
    private profileListener myListener;
    EditText name,rno,dept,about,email,phone,address;
    Boolean imageChanged = false;
    Button editButton;
    private final int PICK_IMAGE_REQUEST = 1;
    View.OnClickListener imagelistener;
    CircleImageView profileImage;
    private Uri filePath;
    ListenerRegistration myDbList;
    FirebaseAuth auth;
    FirebaseFirestore db;
    int editMode=0;
    interface profileListener{
        void setActionBarTitle(String title);
        void disableDrawer(boolean enabled);
        void refreshData();
        void makeSnackBar(String msg);
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, viewGroup, false);
        name = view.findViewById(R.id.myBasicName);
        myListener = (profileListener) getActivity();
        if (myListener != null) {
            myListener.makeLoadingSnackBar("Loading Profile..");
        }
        rno= view.findViewById(R.id.myBasicRoll);
        dept= view.findViewById(R.id.myBasicDept);
        editButton= view.findViewById(R.id.Editbutton);
        about = view.findViewById(R.id.about);
        email = view.findViewById(R.id.myContactEmail);
        phone = view.findViewById(R.id.myContactPhone);
        address = view.findViewById(R.id.myContactAddress);
        auth =FirebaseAuth.getInstance();
        db =FirebaseFirestore.getInstance();
        profileImage = view.findViewById(R.id.pfImageView);
        loadUserData();
        imagelistener = (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (editMode == 1) {
                        editMode = 0;
                        if(name.getText().toString().length() > 20)
                        {
                            Toast.makeText(getActivity(),"Name too long (Max 20 characters)!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        updateUserDate();
                        disableAll();
                    } else {
                        editMode = 1;
                        myListener.makeSnackBar("Editing Mode Enabled!");
                        myDbList.remove();
                        enableAll();
                    }
                }
        });
        setHasOptionsMenu(true);
        assert myListener != null;
        myListener.setActionBarTitle("My Profile");
        return view;
    }
    public void loadUserData() {
        myDbList = db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            UserInfo uinfo = documentSnapshot.toObject(UserInfo.class);
                            if (uinfo != null) {
                                name.setText(uinfo.getName());
                                rno.setText(uinfo.getRno());
                                dept.setText(uinfo.getDept_name());
                                about.setText(uinfo.getAbout());
                                email.setText(uinfo.getEmail());
                                address.setText(uinfo.getAddress());
                                phone.setText((uinfo.getPhone()));
                                if (isAdded()) {
                                    GlideApp.with(FragmentProfile.this)
                                            .load(uinfo.getUimage())
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    myListener.makeSnackBar("Failed to load image (Network Error)");
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    myListener.dismissSnackBar();
                                                    return false;
                                                }
                                            })
                                            .placeholder(R.drawable.defaultpic)
                                            .into(profileImage);
                                }
                            }
                        }
                    }
                });
    }
    public void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void updateUserDate()
    {
        FirebaseAuth auth;
        FirebaseFirestore db;
        auth =FirebaseAuth.getInstance();
        db =FirebaseFirestore.getInstance();
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("name",name.getText().toString());
        newData.put("about",about.getText().toString());
        newData.put("address",address.getText().toString());
        newData.put("phone",phone.getText().toString());
        if(imageChanged)
        {
            try {
                uploadAndSaveImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageChanged=false;
        }
        else
            myListener.makeSnackBar("(Data Saved) Editing Mode Disabled!");
        DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        docRef.update(newData);
    }
    public void uploadAndSaveImage() throws IOException {
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        if(filePath != null)
        {
            editButton.setEnabled(false);
            myListener.disableDrawer(false);
            final Snackbar sb = Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.drawer_layout), "Uploading Image Please Wait", Snackbar.LENGTH_INDEFINITE);
            sb.show();
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            final StorageReference ref = storageReference.child("user_profile_pic/"+ Objects.requireNonNull(auth.getCurrentUser()).getUid());
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String,Object> myMap = new HashMap<>();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference docRef = db.collection("users").document(auth.getCurrentUser().getUid());
                                    myMap.put("uimage",uri.toString());
                                    docRef.update(myMap);
                                    sb.dismiss();
                                    myListener.makeSnackBar("(Data Saved) Editing Mode Disabled!");
                                    myListener.disableDrawer(true);
                                    editButton.setEnabled(true);
                                    myListener.refreshData();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            myListener.makeSnackBar("Error Try Again!");
                            myListener.disableDrawer(true);
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.sortElements).setVisible(false);
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        menu.findItem(R.id.searchButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
    public void disableAll()
    {
        name.setEnabled(false);
        address.setEnabled(false);
        phone.setEnabled(false);
        rno.setEnabled(false);
        dept.setEnabled(false);
        about.setEnabled(false);
        profileImage.setOnClickListener(null);
    }
    public void enableAll()
    {
        name.setEnabled(true);
        address.setEnabled(true);
        phone.setEnabled(true);
        about.setEnabled(true);
        name.requestFocus();
        profileImage.setOnClickListener(imagelistener);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
                imageChanged = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
