package com.example.root.makingit;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FragmentDept extends Fragment {
    CardView collegeCardView;
    TextView collegeName,collegeAddress,collegeAbout,collegeRating,popularCourses;
    ImageView collegeImage;
    Button checkYes,checkNo;
    RecyclerView collegeRecycler;
    List<CollegeListSearchModel> collegeSearchList = new ArrayList<>();
    CollegeSearchListAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    SearchView collegeSearch;
    departmentListener myListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private CollectionReference collegeDBRef = db.collection("institute_list");

    interface departmentListener{
        void collegeUpdated();
        void setActionBarTitle(String title);
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        myListener = (departmentListener) getActivity();
        View view = inflater.inflate(R.layout.fragment_dept,viewGroup, false);
        collegeName = view.findViewById(R.id.collegeName);
        collegeAbout = view.findViewById(R.id.collegeDetail);
        collegeRating = view.findViewById(R.id.ratingText);
        collegeCardView = view.findViewById(R.id.collegeCardView);
        collegeAddress =view.findViewById(R.id.locationText);
        popularCourses = view.findViewById(R.id.popularCourseText);
        collegeImage = view.findViewById(R.id.instituteImageView);
        checkYes = view.findViewById(R.id.checkYes);
        checkNo = view.findViewById(R.id.checkNo);
        checkForCollege(view);
        if (myListener != null) {
            myListener.setActionBarTitle("My College");
        }
        return view;
    }
    public  void setUpFragments(View view)
    {
        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    public void checkForCollege(final View view)
    {
        db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("dept")==null)
                {
                    viewPager = view.findViewById(R.id.viewpager);
                    tabLayout = view.findViewById(R.id.tabs);
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    doFindCollege(view);
                }
                else
                {
                    RelativeLayout setupCollege = view.findViewById(R.id.setUpCollege);
                    setupCollege.setVisibility(View.GONE);
                    setUpFragments(view);
                }
            }
        });
    }
    public  void doFindCollege(View view)
    {
        Query query = collegeDBRef;
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collegeSearchList.add(documentSnapshot.toObject(CollegeListSearchModel.class));
                }
            }
        });
        collegeSearch = view.findViewById(R.id.collegeSearchView);
        collegeRecycler = view.findViewById(R.id.collegeRecyclerList);
        collegeRecycler.setVisibility(View.GONE);
        collegeSearch.setSubmitButtonEnabled(true);
        collegeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }
    public void searchList(String searchText) {
        if(searchText!=null && !searchText.equals("")){
        searchText = searchText.toUpperCase();
        final List<CollegeListSearchModel> resultList = new ArrayList<>();
        for (CollegeListSearchModel user : collegeSearchList)  {
            if (user.getCollegeName() != null && user.getCollegeName().contains(searchText) || user.getCollegeAddress().contains(searchText)) {
                resultList.add(user);
            }
        }
        collegeRecycler.setVisibility(View.VISIBLE);
        adapter = new CollegeSearchListAdapter(resultList,getContext());
        collegeRecycler.setAdapter(adapter);
        collegeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setOnItemClickListener(new CollegeSearchListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    CollegeListSearchModel model =resultList.get(position);
                    db.collection("institute_list").document(model.getCollegeId())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final CollegeInfo collegeInfo = documentSnapshot.toObject(CollegeInfo.class);
                            if(collegeInfo!=null) {
                                collegeName.setText(collegeInfo.getCollegeName());
                                collegeAbout.setText(collegeInfo.getCollegeAbout());
                                collegeRating.setText(collegeInfo.getCollegeRating());
                                collegeAddress.setText(collegeInfo.getCollegeAddress());
                                popularCourses.setText(collegeInfo.getPopularCourses());
                            }
                            if(collegeInfo!=null && collegeInfo.getCollegeImage() != null)
                            {
                                if(getActivity()!=null) {
                                    GlideApp.with(getActivity().getApplicationContext())
                                            .load(collegeInfo.getCollegeImage())
                                            .placeholder(R.drawable.loadme)
                                            .into(collegeImage);
                                    collegeImage.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                collegeImage.setVisibility(View.GONE);
                            }
                            checkYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map<String,Object> myMap = new HashMap<>();
                                    if(collegeInfo!=null) {
                                        myMap.put("dept", collegeInfo.getCollegeId());
                                        myMap.put("dept_name", collegeInfo.getCollegeName());
                                    }
                                    if(auth.getCurrentUser()!=null)
                                    db.collection("users").document(auth.getCurrentUser().getUid())
                                            .update(myMap);
                                    myListener.collegeUpdated();

                                }
                            });
                            checkNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Animation slidedown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                                    collegeCardView.setVisibility(View.INVISIBLE);
                                    collegeCardView.startAnimation(slidedown);
                                    collegeRecycler.setVisibility(View.VISIBLE);
                                    collegeSearch.setVisibility(View.VISIBLE);
                                }
                            });
                            Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                            collegeRecycler.setVisibility(View.GONE);
                            collegeSearch.setVisibility(View.GONE);
                            collegeCardView.setVisibility(View.VISIBLE);
                            collegeCardView.startAnimation(slideUp);
                        }
                    });

                }
            });
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentDeptNotice(), "Events");
        adapter.addFragment(new FragmentDeptOther(), "Peers");
        adapter.addFragment(new FragmentDeptSubject(), "Reviews");
        adapter.addFragment(new FragmentDeptSubject(), "Alumni");
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}