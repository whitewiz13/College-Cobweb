package com.example.root.makingit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class InstituteMainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ActionBar actionbar;
    AppBarLayout appBarLayout;
    ViewPager viewPager;
    CollegeInfo collegeInfo;
    CollapsingToolbarLayout collapsingToolbarLayout;
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
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.app_bar);
        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionbar = getSupportActionBar();
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fabMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
    public void loadData(String id)
    {
        collectionReference.document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null)
                    collegeInfo = documentSnapshot.toObject(CollegeInfo.class);
                if(collegeInfo!=null)
                    setUpActionBar(actionbar,collegeInfo);
            }
        });
    }
    //Setup Actionbar
    public void setUpActionBar(ActionBar actionbar,CollegeInfo collegeInfo)
    {
        GlideApp.with(this)
                .asBitmap()
                .load(collegeInfo.getCollegeImage())
                .placeholder(R.drawable.loadme)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull  Bitmap resource, Transition<? super Bitmap> transition) {
                        Drawable dr = new BitmapDrawable(resource);
                        collapsingToolbarLayout.setBackground(dr);
                    }
                });
        collapsingToolbarLayout.setTitle(collegeInfo.getCollegeName());
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarThemeAppBar);
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentInstituteAbout(), "About");
        adapter.addFragment(new FragmentInstituteCourse(), "Courses");
        adapter.addFragment(new FragmentInstituteMore(), "Alumni");
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