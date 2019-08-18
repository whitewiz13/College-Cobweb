package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {
    int RC_SIGN_IN = 2;
    FloatingActionButton fabGoogle;
    FirebaseMessaging firebaseMessagingService;
    private String userName=null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText email,pass;
    Button skip;
    Button login;
    private ProgressBar progressBar;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
            return;
        }
        if (user!=null && user.isAnonymous())
        {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
            return;
        }
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.editText);
        pass = findViewById(R.id.editText2);
        fabGoogle = findViewById(R.id.googleSign);
        progressBar = findViewById(R.id.progressBBar);
        login = findViewById(R.id.submit);
        skip = findViewById(R.id.skip);
        final TextView register = findViewById(R.id.register);
        login.setOnClickListener(loginClickListener);
        fabGoogle.setOnClickListener(gooleSignInListener);
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    doLogin();
                    handled = true;
                }
                return handled;
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "Logged in as Guest User!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    View.OnClickListener gooleSignInListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };
    //LoginButton Listener
    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            doLogin();
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
              progressBar.setVisibility(View.GONE);
            }
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        db.collection("taken_email").document(acct.getEmail()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            Toast.makeText(MainActivity.this,"Account exists with email! Link your account after logging in!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else
                        {
                            createAccount(credential,acct);
                        }
                    }
                }
        );
    }
    public void createAccount(final AuthCredential credential, final GoogleSignInAccount account)
    {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            checkIfReal(account);
                            Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void checkIfReal(final GoogleSignInAccount account)
    {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (!document.exists()) {
                        WriteBatch batch = db.batch();
                        DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        UserInfo userInfo = new UserInfo(docRef.getId(),account.getDisplayName(),"Not Available",null,account.getEmail(),account.getPhotoUrl().toString(),null);
                        batch.set(docRef,userInfo);
                        batch.commit();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    else
                    {
                        startActivity(new Intent(MainActivity.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }
        });
    }
    public void doLogin()
    {
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
        if(!isValidEmail(eemail))
        {
            progressBar.setVisibility(View.VISIBLE);
            tryUsername(eemail,ppass);
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
    public void tryUsername(final String username,final String ppass)
    {
        try {
            db.collection("taken_rno").document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String uid = documentSnapshot.getString("more_stuff");
                    try {
                            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    userName = documentSnapshot.getString("email");
                                        auth.signInWithEmailAndPassword(userName, ppass)
                                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (!task.isSuccessful()) {
                                                            if (ppass.length() < 6) {
                                                                pass.setError(getString(R.string.minimun_length));
                                                            } else {
                                                                Toast.makeText(MainActivity.this, getString(R.string.auth_failed) + " " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Intent intent = new Intent(MainActivity.this, Home.class);
                                                            startActivity(intent);
                                                            finish();
                                                            Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                                                            performSubscription();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Invalid Username!",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }catch (Exception e)
        {

            Toast.makeText(getApplicationContext(),"Invalid Username!",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }
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
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
