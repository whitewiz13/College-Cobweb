package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.Write;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class Register extends AppCompatActivity {
    FirebaseMessaging firebaseMessagingService;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private EditText email, password;
    private EditText fname,rollnumber;
    private FirebaseAuth auth;
    private Button btnreg;
    private CircleImageView imageView;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    String fn,rno;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.item_fall_down, R.anim.slide_out);
        setContentView(R.layout.register);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView = findViewById(R.id.peerProfileImage);
        auth = FirebaseAuth.getInstance();
        fname = findViewById(R.id.name);
        rollnumber = findViewById(R.id.rollno);
        email = findViewById(R.id.rname);
        password= findViewById(R.id.rpass);
        btnreg= findViewById(R.id.reg);
        progressBar = findViewById(R.id.progressBBar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputManager != null;
                inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                String eemail = email.getText().toString().trim();
                String ppass = password.getText().toString().trim();
                if (TextUtils.isEmpty(eemail)) {
                    showToast("Enter Email address!");
                    btnreg.setEnabled(true);
                    return;
                }
                if (TextUtils.isEmpty(ppass)) {
                    showToast("Enter Password!");
                    btnreg.setEnabled(true);
                    return;
                }
                if (ppass.length() < 6) {
                    showToast("Password too short, enter minimum 6 characters!");
                    btnreg.setEnabled(true);
                    return;
                }
                rno=rollnumber.getText().toString();
                checkRNumTakenAndSave(rno,eemail,ppass);

            }
        });
    }
    public void checkRNumTakenAndSave(String rnoo,final String eemail,final String ppass)
    {
        progressBar.setVisibility(View.VISIBLE);
        btnreg.setEnabled(false);
        db.collection("taken_rno").document(rnoo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists())
                    {
                        showToast("Username Already Exists!");
                        progressBar.setVisibility(View.GONE);
                        btnreg.setEnabled(true);
                    }
                    else
                    {
                        auth.createUserWithEmailAndPassword(eemail, ppass)
                                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                fn = fname.getText().toString().trim();
                                                rno = rollnumber.getText().toString().trim();
                                                try {
                                                    uploadImageAndSave(fn, rno);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (!task.isSuccessful()) {
                                                showToast(Objects.requireNonNull(task.getException()).getMessage());
                                                btnreg.setEnabled(true);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Register.this.overridePendingTransition(R.anim.item_fall_down,
                R.anim.slide_out);
    }
    public void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    /*
    public void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        showToast("Email Sent for verification!");
                    }
                }
            });
        }
    }*/
    private void uploadImageAndSave(final String fname,final String rollnumber) throws IOException {
        final WriteBatch batch;
        batch = db.batch();
        if(filePath != null)
        {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
                                    String eemail = email.getText().toString().trim();
                                    Map<String,Object> myMap = new HashMap<>();
                                    Map<String,Object> myEmailMap = new HashMap<>();
                                    DocumentReference docRef = db.collection("users").document(auth.getCurrentUser().getUid());
                                    UserInfo user = new UserInfo(docRef.getId(),fname,rollnumber,null,eemail,uri.toString(),null);
                                    myMap.put("more_stuff",auth.getCurrentUser().getUid());
                                    myEmailMap.put("more_stuff",auth.getCurrentUser().getUid());
                                    batch.set(docRef,user);
                                    batch.set(db.collection("taken_rno").document(rollnumber),myMap);
                                    batch.set(db.collection("taken_email").document(eemail),myEmailMap);
                                    batch.commit();
                                    showToast("Account Created Successfully!");
                                    progressBar.setVisibility(View.GONE);
                                    //sendVerificationEmail();
                                    startActivity(new Intent(Register.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            String eemail = email.getText().toString().trim();
            Map<String,Object> myMap = new HashMap<>();
            Map<String,Object> myEmailMap = new HashMap<>();
            DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
            UserInfo user = new UserInfo(docRef.getId(),fname,rollnumber,null,eemail,"https://i.stack.imgur.com/34AD2.jpg",null);
            myMap.put("more_stuff",auth.getCurrentUser().getUid());
            myEmailMap.put("more_stuff",auth.getCurrentUser().getUid());
            batch.set(docRef,user);
            batch.set(db.collection("taken_rno").document(rollnumber),myMap);
            batch.set(db.collection("taken_email").document(eemail),myEmailMap);
            batch.commit();
            showToast("Account Created Successfully!");
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(Register.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }/*
    public void performSubscription(String dept)
    {
        firebaseMessagingService = FirebaseMessaging.getInstance();
        firebaseMessagingService.subscribeToTopic("pushEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCITEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCMATHEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCPHYSICSEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCCHEMISTRYEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCZOOLOGYEvent");
        firebaseMessagingService.subscribeToTopic("push"+dept+"Event");
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}