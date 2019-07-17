package com.example.root.makingit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileOpenActivity extends AppCompatActivity {
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    String authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    TextView uNameAct,uRnoAct,uDeptAct,uAboutAct;
    CircleImageView uImageAct;
    Toolbar tb;
    Button sendFirstMessage;
    Snackbar sbView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        sbView = Snackbar.make(findViewById(R.id.userprofileinfolayout), "No internet connection!", Snackbar.LENGTH_INDEFINITE);
        tb = findViewById(R.id.userProfileToolbar);
        setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("User Profile");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
        sendFirstMessage = findViewById(R.id.sendFirstMessage);
        uNameAct = findViewById(R.id.userNameAct);
        uRnoAct = findViewById(R.id.userRnoAct);
        uDeptAct = findViewById(R.id.userDeptAct);
        uAboutAct = findViewById(R.id.userAboutAct);
        uImageAct = findViewById(R.id.userProfileImageAct);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String id = extras.getString("userId");
            loadUserData(id);
            sendFirstMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!authId.equals(id))
                        addToMainChat(id);
                    else
                       Toast.makeText(UserProfileOpenActivity.this,"You can't message yourself",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void addToMainChat(final String id)
    {

        db.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final DocumentSnapshot documentSnapshotName = task.getResult();
                db.collection("users").document(authId)
                        .collection("chats").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && !documentSnapshot.exists()) {
                            if (documentSnapshotName != null) {
                                db.collection("users").document(authId)
                                        .collection("chats").document(id).set(new ChatMainModel(documentSnapshotName.getString("name")
                                        , "Start a conversation", id));
                            }
                        }
                    }
                });
            }
        });
        Intent i = new Intent(this, ChatScreenActivity.class);
        i.putExtra("userId",id);
        startActivity(i);
    }
    public void loadUserData(String id)
    {
        db.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    UserInfo uinfo = documentSnapshot.toObject(UserInfo.class);
                    if (uinfo != null) {
                        uNameAct.setText(uinfo.getName());
                        uRnoAct.setText(uinfo.getRno());
                        uDeptAct.setText(uinfo.getDept());
                        uAboutAct.setText(uinfo.getAbout());
                            GlideApp.with(getApplicationContext())
                                    .load(uinfo.getUimage())
                                    .placeholder(R.drawable.loadme)
                                    .into(uImageAct);
                    }
                }
            }
        });
    }
    public void onBackPressed()
    {
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras()!=null) {
                NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                    sbView.dismiss();
                }
                else{
                    sbView.getView().setBackgroundColor(Color.RED);
                    sbView.show();
                }
            }
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }
}