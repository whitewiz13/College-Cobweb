package com.example.root.makingit;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends AppCompatActivity implements FragmentEvent.onDoStuffForActivity,FragmentAddEvent.onActionListener
,FragmentDept.departmentListener,FragmentProfile.profileListener,FragmentDeptAddEvent.onActionListener
,FragmentForum.onDoStuffForActivity,FragmentDeptNotice.departmentListener,FragmentAddForum.onForumAdded
,FragmentBrowseInstitute.onDoStuffForActivity{

    String fTag="fEvent";
    Snackbar loadingSnack;
    Snackbar sbView;
    private DrawerLayout mDrawerLayout;
    private TextView uname,rno,dept;
    Toolbar tb;
    Fragment fragment;
    DialogFragment frag;
    CircleImageView profileImage;
    private FirebaseAuth auth =FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActionBar actionbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final View headerview = navigationView.getHeaderView(0);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionbar = getSupportActionBar();
        setUpActionBar(actionbar);
        uname = headerview.findViewById(R.id.suname);
        rno = headerview.findViewById(R.id.surno);
        dept = headerview.findViewById(R.id.sudept);
        profileImage = headerview.findViewById(R.id.peerProfileImage);
        Bundle extras = getIntent().getExtras();
        if(auth.getCurrentUser()!=null && !auth.getCurrentUser().isAnonymous()) {
            navigationView.inflateMenu(R.menu.drawer_view);
            FirebaseMessaging.getInstance().subscribeToTopic("pushChatNotification");
            checkIfReal();
            checkInternetConnection();
            dismissNotifications();
            //checkVerifiedEmail();
            loadUserData();
            fragment = new FragmentEvent();
            setFragment(fragment, "fEvent");
            navigationView.setNavigationItemSelectedListener(drawerItemSelect);
            mDrawerLayout.addDrawerListener(drawerStateListener);
            checkToOpenChat(extras);
        }
        else
        {
            fragment = new FragmentBrowseInstitute();
            setFragment(fragment,"fBrowseIn");
            navigationView.inflateMenu(R.menu.guest_drawer);
            setUpGuest(new UserInfo("-","GUEST","-","-","-","-"));
            navigationView.setNavigationItemSelectedListener(guestDrawerListener);
            mDrawerLayout.addDrawerListener(drawerStateListener);
        }
    }
    //Setting up guest
    public void setUpGuest(UserInfo uInfo)
    {
        uname.setText(uInfo.getName());
        rno.setText(uInfo.getRno());
        dept.setText(uInfo.getDept());
        GlideApp.with(getApplicationContext())
                .load(R.drawable.defaultpic)
                .placeholder(R.drawable.defaultpic)
                .into(profileImage);
    }
    //Setup Actionbar
    public void setUpActionBar(ActionBar actionbar)
    {
        if (actionbar != null) {
            actionbar.setTitle("Home");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }
    //Listeners for navigation drawers
    DrawerLayout.DrawerListener drawerStateListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }
        @Override
        public void onDrawerStateChanged(int newState) { }
        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            if(!auth.getCurrentUser().isAnonymous())
                checkUserName();
        }
        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            if (fragment != null &&!fragment.isAdded()) {
                setFragment(fragment,fTag);
                fragment=null;
            }
        }
    };
    //For Guest Menu
    NavigationView.OnNavigationItemSelectedListener guestDrawerListener= new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            int id= menuItem.getItemId();
            switch (id)
            {
                case R.id.nav_browse_college:
                    fragment = new FragmentBrowseInstitute();
                    fTag = "fBrowseIn";
                    break;
                case R.id.nav_Sign_in:
                    auth.getCurrentUser().delete();
                    auth.signOut();
            }
            return true;
        }
    };
    NavigationView.OnNavigationItemSelectedListener drawerItemSelect = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            int id= menuItem.getItemId();
            switch (id)
            {
                case R.id.nav_myprofile:
                    fragment= new FragmentProfile();
                    fTag = "fProfile";
                    break;
                case R.id.nav_events:
                    fragment = new FragmentEvent();
                    fTag = "fEvent";
                    break;
                case R.id.nav_dept:
                    fragment = new FragmentDept();
                    fTag = "fDept";
                    break;
                case R.id.nav_forum:
                    fragment = new FragmentForum();
                    fTag = "fForum";
                    break;
                case R.id.nav_chat:
                    fragment = new FragmentChat();
                    fTag = "fChat";
                    break;
                case R.id.nav_signout:
                    auth.signOut();
                    break;

            }
            return true;
        }
    };
    //Checking for user authentication!
    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
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
    private void checkInternetConnection() {
        sbView = Snackbar.make(findViewById(R.id.drawer_layout), "No internet connection!", Snackbar.LENGTH_INDEFINITE);
        if(haveNetworkConnection())
        {
            sbView.dismiss();
        }
        else
        {
            sbView.getView().setBackgroundColor(Color.RED);
            sbView.show();
        }
    }
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = new NetworkInfo[0];
        if (cm != null) {
            netInfo = cm.getAllNetworkInfo();
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void checkToOpenChat(Bundle extras) {
        if (extras != null) {
            String chatOpen = extras.getString("fragmentChat");
            if (chatOpen != null) {
                fragment = new FragmentChat();
                setFragment(fragment,"fChat");
            }
        }
    }
    public void dismissNotifications()
    {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null) {
            notificationManager.cancelAll();
            MyFirebaseMessagingService.count = 0;
            MyFirebaseMessagingService.deptcount = 0;
            MyFirebaseMessagingService.chatcount =0;
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
                        auth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(Home.this, Register.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "Error Occurred Please Register again!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        checkUserName();
                    }
                }
            }
        });
    }
    public void loadUserData()
    {
        final DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot snapshot,
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
    public void checkUserName()
    {
        if(auth.getCurrentUser()!=null) {
            final String uid = auth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null) {
                                db.collection("taken_rno").document(Objects.requireNonNull(snapshot.getString("rno"))).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if (documentSnapshot != null && !Objects.equals(documentSnapshot.getString("more_stuff"), uid)) {
                                                    changeUserName();
                                                }
                                            }
                                        });
                            }
                        }
                    }
            );
        }
    }
    public void changeUserName()
    {
        frag=new FragmentChangeUserName();
        frag.setCancelable(false);
        setDialogFragment(frag);
    }
    public void setFragment(Fragment fragment,String tag)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        transaction.add(fragment,tag);
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
    public void makeLoadingSnackBar(String msg) {
        if(haveNetworkConnection()) {
            loadingSnack = Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_INDEFINITE);
            loadingSnack.show();
        }
    }

    @Override
    public void dismissSnackBar() {
        if (haveNetworkConnection()) {
            if(loadingSnack!=null)
                loadingSnack.dismiss();
        }
    }

    @Override
    public void tellAboutAddition() {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag("fEvent");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
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
        if(auth.getCurrentUser() !=null && !auth.getCurrentUser().isAnonymous())
            checkUserName();
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
            case R.id.sortLoc:
                if(!item.isChecked()) {
                    FragmentBrowseInstitute fragmentBrowseInstitute = (FragmentBrowseInstitute) getSupportFragmentManager().findFragmentByTag("fBrowseIn");
                    fragmentBrowseInstitute.loadInstituteList("collegeAddress", Query.Direction.ASCENDING);
                    item.setChecked(true);
                }
                break;
            case R.id.sortRating:
                if(!item.isChecked()) {
                    FragmentBrowseInstitute fragmentBrowseInstitute = (FragmentBrowseInstitute) getSupportFragmentManager().findFragmentByTag("fBrowseIn");
                    fragmentBrowseInstitute.loadInstituteList("collegeRating", Query.Direction.DESCENDING);
                    item.setChecked(true);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public void makeSnackBar(String msg) {
        if(haveNetworkConnection()) {
            Snackbar sb = Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_LONG);
            sb.show();
        }
    }
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras()!=null && auth.getCurrentUser()!=null && !auth.getCurrentUser().isAnonymous()) {
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
    public void onBackPressed()
    {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
        else {
            finish();
        }
    }
    /*public void checkVerifiedEmail()
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
   }*/

    //Override methods for different phases in the activity
    @Override
    public void dismissMe(DialogFragment frag)
    { dismissDialogFragment(frag); }
    @Override
    public void makeSnackB(String msg)
    { makeSnackBar(msg); }

    @Override
    public void addedForumPost() {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag("fForum");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    public void onStart() { super.onStart();
        auth.addAuthStateListener(authListener); }
    @Override
    public void onStop() { super.onStop();
        if (authListener != null) { auth.removeAuthStateListener(authListener); } }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(auth.getCurrentUser()!=null &&!auth.getCurrentUser().isAnonymous())
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        else
            getMenuInflater().inflate(R.menu.guest_toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu); }
    @Override
    protected void onPause() { super.onPause();
        unregisterReceiver(networkReceiver); }
    @Override
    protected void onResume() { super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter); }
}