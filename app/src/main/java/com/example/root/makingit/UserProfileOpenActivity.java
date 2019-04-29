package com.example.root.makingit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileOpenActivity extends AppCompatActivity {
    TextView uNameAct,uRnoAct,uDeptAct,uAboutAct;
    CircleImageView uImageAct;
    Toolbar tb;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
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
        uNameAct = findViewById(R.id.userNameAct);
        uRnoAct = findViewById(R.id.userRnoAct);
        uDeptAct = findViewById(R.id.userDeptAct);
        uAboutAct = findViewById(R.id.userAboutAct);
        uImageAct = findViewById(R.id.userProfileImageAct);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("userId");
            loadUserData(id);
        }
    }
    public void loadUserData(String id)
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}