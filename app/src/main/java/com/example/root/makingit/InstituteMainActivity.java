package com.example.root.makingit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class InstituteMainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ActionBar actionbar;
    ViewPager viewPager;
    CollegeInfo collegeInfo;
    Toolbar tb;
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("institute_list");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.institute_main_activity);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("collegeId");
            loadData(id);
        }
        tabLayout = findViewById(R.id.instituteTab);
        viewPager = findViewById(R.id.instituteViewPager);
        tb = findViewById(R.id.instituteToolbar);
        setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionbar = getSupportActionBar();
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }
    public void loadData(String id)
    {
        collectionReference.document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null)
                    collegeInfo = documentSnapshot.toObject(CollegeInfo.class);
                if(collegeInfo!=null)
                    setUpActionBar(actionbar,collegeInfo.getCollegeName());
            }
        });
    }
    //Setup Actionbar
    public void setUpActionBar(ActionBar actionbar,String title)
    {
        if (actionbar != null) {
            actionbar.setTitle(title);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentInstituteAbout(), "About");
        adapter.addFragment(new FragmentInstituteCourse(), "Courses");
        adapter.addFragment(new FragmentInstituteMore(), "Faculty");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
