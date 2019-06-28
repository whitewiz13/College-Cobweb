package com.example.root.makingit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends AppCompatActivity implements FragmentEvent.onDoStuffForActivity,FragmentAddEvent.onActionListener
,FragmentDept.departmentListener,FragmentProfile.profileListener,FragmentDeptAddEvent.onActionListener
,FragmentForum.onDoStuffForActivity{

    private DrawerLayout mDrawerLayout;
    private TextView uname,rno,dept;
    Toolbar tb;
    Fragment fragment;
    DialogFragment frag;
    CircleImageView profileImage;
    private FirebaseAuth auth;
    ActionBar actionbar;
    private FirebaseAuth.AuthStateListener authListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic("pushEvent");
        auth = FirebaseAuth.getInstance();
        checkIfReal();
        setContentView(R.layout.home);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final View headerview = navigationView.getHeaderView(0);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("Home");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        uname = headerview.findViewById(R.id.suname);
        rno = headerview.findViewById(R.id.surno);
        dept = headerview.findViewById(R.id.sudept);
        profileImage = headerview.findViewById(R.id.peerProfileImage);
        checkIfReal();
        //checkVerifiedEmail();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Home.this, MainActivity.class));
                    finish();
                    Toast.makeText(getApplicationContext(), "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        loadUserData();
        fragment = new FragmentEvent();
        setFragment(fragment);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        int id= menuItem.getItemId();
                        switch (id)
                        {
                            case R.id.nav_myprofile:
                                fragment= new FragmentProfile();
                                break;
                            case R.id.nav_events:
                                fragment = new FragmentEvent();
                                break;
                            case R.id.nav_dept:
                                fragment = new FragmentDept();
                                break;
                            case R.id.nav_forum:
                                fragment = new FragmentForum();
                                break;
                            case R.id.nav_signout:
                                auth.signOut();
                                break;
                        }
                        return true;
                    }
                });
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }
            @Override
            public void onDrawerStateChanged(int newState) { }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (fragment != null) {
                   setFragment(fragment);
                   fragment=null;
                }
            }
        });
    }
    public void checkVerifiedEmail()
    {
        FirebaseUser user = auth.getCurrentUser();
        Snackbar sbView = Snackbar.make(findViewById(R.id.drawer_layout), "Email not verified!", Snackbar.LENGTH_INDEFINITE);
        if(!user.isEmailVerified())
        {
            sbView.getView().setBackgroundColor(Color.RED);
            sbView.show();
        }
        else {
            sbView.dismiss();
        }
    }
    public void checkIfReal()
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
                        auth.getCurrentUser().delete();
                        startActivity(new Intent(Home.this, Register.class));
                        finish();
                        Toast.makeText(getApplicationContext(), "Error Occurred Please Register again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void loadUserData()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    uname.setText(snapshot.getString("name"));
                    rno.setText(snapshot.getString("rno"));
                    dept.setText(snapshot.getString("dept"));
                    GlideApp.with(getApplicationContext())
                            .load(snapshot.getString("uimage"))
                            .placeholder(R.drawable.defaultpic)
                            .into(profileImage);
                }
            }
        });
    }
    public void setFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        transaction.replace(R.id.fragmentMain,fragment);
        transaction.commit();
    }
    public void setDialogFragment(DialogFragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragment.show(transaction,"Add Event");
    }
    public void dismissDialogFragment(DialogFragment fragment)
    {
        fragment.dismiss();
    }
    @Override
    public void setActionBarTitle(String title)
    {
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void disableDrawer(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        mDrawerLayout.setDrawerLockMode(lockMode);
        actionbar.setDisplayHomeAsUpEnabled(enabled);
    }

    @Override
    public void refreshData() {
        loadUserData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addEventButton:
                frag=new FragmentAddEvent();
                setDialogFragment(frag);
                break;
            case R.id.addDeptEventButton:
                frag = new FragmentDeptAddEvent();
                setDialogFragment(frag);
                break;
            case R.id.addForumPostButton:
                frag = new FragmentAddForum();
                setDialogFragment(frag);
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed()
    {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
        else
            finish();
    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    public void makeSnackBar(String msg) {
        Snackbar sb = Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_LONG);
        sb.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void dismissMe(DialogFragment frag)
    {
        dismissDialogFragment(frag);
    }
    @Override
    public void makeSnackB(String msg)
    {
        makeSnackBar(msg);
    }
}