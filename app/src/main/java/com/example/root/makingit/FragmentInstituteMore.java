package com.example.root.makingit;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FragmentInstituteMore extends Fragment {
    TextView totalRatingNum,numberReviews;
    int fiveStar=0,fourStar=0,threeStar=0,twoStar=0,oneStar=0;
    ImageView starOne,starTwo,starThree,starFour,starFive;
    RecyclerView reviewRecycler;
    String id = null;
    ReviewsAdapter adapter;
    ProgressBar subjectProgress;
    RelativeLayout reviewMainHead;
    int oldRated=-1;
    int oldPos = -1;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reviewRef = db.collection("institute_list");
    List<ReviewModel> reviewList  = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        try {
            if (getArguments() != null) {
                id = getArguments().getString("collegeId");
            }
        }
        catch (Exception e)
        {
            //Later
        }
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_institute_more, viewGroup, false);
        starOne = view.findViewById(R.id.totalStarFirst);
        starTwo = view.findViewById(R.id.totalStarSecond);
        starThree = view.findViewById(R.id.totalStarThird);
        starFour = view.findViewById(R.id.totalStarFourth);
        starFive = view.findViewById(R.id.totalStarFifth);
        numberReviews = view.findViewById(R.id.numberOfReviews);
        totalRatingNum = view.findViewById(R.id.totalRatingNumber);
        subjectProgress = view.findViewById(R.id.deptSubjectProgress);
        reviewRecycler = view.findViewById(R.id.reviewList);
        reviewMainHead = view.findViewById(R.id.reviewMainHead);
        loadReviewList();
        return view;
    }
    public  void  loadTotalRating(final String dept)
    {
        subjectProgress.setVisibility(View.VISIBLE);
        reviewRef.document(dept).collection("rating")
                .document("rating_rev").collection("5")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ignored : queryDocumentSnapshots)
                {
                    fiveStar++;
                }
                reviewRef.document(dept).collection("rating")
                        .document("rating_rev").collection("4")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ignored : queryDocumentSnapshots)
                        {
                            fourStar++;
                        }
                        reviewRef.document(dept).collection("rating")
                                .document("rating_rev").collection("3")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot ignored : queryDocumentSnapshots)
                                {
                                    threeStar++;
                                }
                                reviewRef.document(dept).collection("rating")
                                        .document("rating_rev").collection("2")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(DocumentSnapshot ignored : queryDocumentSnapshots)
                                        {
                                            twoStar++;
                                        }
                                        reviewRef.document(dept).collection("rating")
                                                .document("rating_rev").collection("1")
                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for(DocumentSnapshot ignored : queryDocumentSnapshots)
                                                {
                                                    oneStar++;
                                                }
                                                double ts = fiveStar+fourStar+threeStar+twoStar+oneStar;
                                                double tt = (5*fiveStar)+(4*fourStar)+(3*threeStar)+(2*twoStar)
                                                        +oneStar;
                                                double tr = tt/ts;
                                                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                                String numberAsString = decimalFormat.format(tr);
                                                totalRatingNum.setText(numberAsString);
                                                numberReviews.setText(String.valueOf((int)ts));
                                                try {
                                                    setStars(tr);
                                                }catch (Exception e)
                                                {

                                                }
                                                subjectProgress.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
    public void setStars(double num)
    {
        switch ((int)num)
        {
            case 5:
                starFive.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starFour.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starThree.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starTwo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starOne.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                break;
            case 4:
                starFive.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starFour.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starThree.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starTwo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starOne.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                break;
            case 3:
                starFive.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starFour.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starThree.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starTwo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starOne.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                break;
            case 2:
                starFive.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starFour.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starThree.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starTwo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                starOne.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                break;
            case 1:
                starFive.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starFour.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starThree.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starTwo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                starOne.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGold)));
                break;
        }
    }
    public void loadReviewList()
    {
        reviewList.clear();
        adapter = new ReviewsAdapter(reviewList,getActivity());
        try {
                    loadTotalRating(id);
                    reviewRef.document(Objects.requireNonNull(id))
                            .collection("reviews").orderBy("rating", Query.Direction.DESCENDING).
                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int pos = 0;
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                ReviewModel rModel = documentSnapshot1.toObject(ReviewModel.class);
                                reviewList.add(rModel);
                                try {
                                    if (rModel != null && (rModel.getAuthorRno().equals(auth.getCurrentUser().getUid()))) {
                                        oldRated = rModel.getRating();
                                        oldPos = pos;
                                    }
                                }catch (Exception e)
                                {
                                    //TODO:Fix This
                                }
                                pos++;
                            }
                            reviewRecycler.setAdapter(adapter);
                        }
                    });
        }catch (Exception e)
        {
            Toast.makeText(getActivity(), "what"+ e,Toast.LENGTH_LONG).show();
        }
        GridLayoutManager gridLayout = new GridLayoutManager(getContext(),1)
        {
            @Override
            public boolean supportsPredictiveItemAnimations()
            {
                return true;
            }
        };
        adapter.setHasStableIds(true);
        reviewRecycler.setLayoutManager(gridLayout);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}