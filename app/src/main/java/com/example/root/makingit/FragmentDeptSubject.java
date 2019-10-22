package com.example.root.makingit;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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


public class FragmentDeptSubject extends Fragment {
    Boolean notLoaded = true;
    TextView totalRatingNum,numberReviews;
    int fiveStar=0,fourStar=0,threeStar=0,twoStar=0,oneStar=0;
    ImageView starOne,starTwo,starThree,starFour,starFive;
    RecyclerView reviewRecycler;
    ReviewsAdapter adapter;
    ProgressBar subjectProgress;
    RelativeLayout reviewMainHead;
    DialogFragment frag;
    int oldRated=-1;
    int oldPos = -1;
    ImageButton leaveReviewButton;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reviewRef = db.collection("institute_list");
    List<ReviewModel> reviewList  = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_dept_subject, viewGroup, false);
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
        leaveReviewButton = view.findViewById(R.id.leaveReviewButton);
        leaveReviewButton.setOnClickListener(leaveReviewButtonClicked);
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
     /*   reviewRef.document(dept).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Double rate = documentSnapshot.getDouble("collegeRating");
                            try {
                            double reviewNum = Objects.requireNonNull(documentSnapshot.getDouble("collegeReview"));
                            totalRatingNum.setText(String.valueOf(rate));
                            numberReviews.setText(String.valueOf((int) reviewNum));
                            setStars(Objects.requireNonNull(rate));
                        }catch (Exception e)
                        {
                            //TODO:Another Exception!
                        }
                    }
                }
        );*/
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
            db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    if(notLoaded) {
                        loadTotalRating(documentSnapshot.getString("dept"));
                        notLoaded = false;

                    }
                    reviewRef.document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                            .collection("reviews").orderBy("rating", Query.Direction.DESCENDING).
                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int pos = 0;
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                ReviewModel rModel = documentSnapshot1.toObject(ReviewModel.class);
                                try {
                                    if (rModel != null && (rModel.getAuthorRno().equals(auth.getCurrentUser().getUid()))) {
                                        oldRated = rModel.getRating();
                                        oldPos = pos;
                                        rModel.setAuthorName(rModel.getAuthorName().concat(" (Your Review)"));
                                        reviewList.add(0,rModel);
                                    }
                                    else
                                    {
                                        reviewList.add(rModel);
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
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(getActivity(), " "+ e,Toast.LENGTH_LONG).show();
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
    View.OnClickListener leaveReviewButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("oldRated", oldRated);
            bundle.putInt("oldPos", oldPos);
            frag = new FragmentLeaveReview();
            frag.setArguments(bundle);
            setDialogFragment(frag);
        }
    };
    public void added(ReviewModel reviewModel)
    {
        subjectProgress.setVisibility(View.VISIBLE);
        loadReviewList();
        notLoaded = false;
        subjectProgress.setVisibility(View.GONE);
    }
    public void setDialogFragment(DialogFragment fragment)
    {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        fragment.show(transaction,"Add Event");
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
