package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseMessaging firebaseMessagingService;
    private EditText email,pass;
    Button login;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
            return;
        }
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.editText);
        pass = findViewById(R.id.editText2);
        progressBar = findViewById(R.id.progressBBar);
        login = findViewById(R.id.submit);
        final TextView register = findViewById(R.id.register);
        login.setOnClickListener(loginClickListener);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });

    }
    //LoginButton Listener
    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            String eemail = email.getText().toString();
            final String ppass = pass.getText().toString();
            if (TextUtils.isEmpty(eemail)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(ppass)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(eemail, ppass)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (ppass.length() < 6) {
                                    pass.setError(getString(R.string.minimun_length));
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.auth_failed)+" " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                                performSubscription();
                            }
                        }
                    });
        }
    };
    public void performSubscription()
    {
        String currentUser =FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db  =FirebaseFirestore.getInstance();
        firebaseMessagingService = FirebaseMessaging.getInstance();
        firebaseMessagingService.subscribeToTopic("pushEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCITEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCMATHEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCPHYSICSEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCCHEMISTRYEvent");
        firebaseMessagingService.unsubscribeFromTopic("pushMSCZOOLOGYEvent");
        if(currentUser!= null)
        db.collection("users").document(currentUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String dept = "";
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    dept = snapshot.getString("dept");
                }
                firebaseMessagingService.subscribeToTopic("push"+dept+"Event");
            }
        });
    }

}
