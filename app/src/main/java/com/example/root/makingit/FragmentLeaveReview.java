package com.example.root.makingit;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class FragmentLeaveReview extends FragmentDeptAddEvent {
    TextView actualReviewText;
    DialogFragment frag = this;
    Button submitReview;
    private onActionListener listner;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    int oldRated=-1;
    ImageButton starOne,starTwo,starThree,starFour,starFive;
    int rating = 1;
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        try {
            if (getArguments() != null) {
                oldRated = getArguments().getInt("oldRated");
            }
        }
        catch (Exception e)
        {
            oldRated = -1;
        }
        View view = inflater.inflate(R.layout.fragment_leave_review, viewGroup, false);
        actualReviewText = view.findViewById(R.id.actualReviewText);
        submitReview = view.findViewById(R.id.submitReview);
        starOne = view.findViewById(R.id.leaveStarFirst);
        listner = (onActionListener) getActivity();
        starTwo = view.findViewById(R.id.leaveStarSecond);
        starThree = view.findViewById(R.id.leaveStarThird);
        starFour = view.findViewById(R.id.leaveStarFourth);
        starFive = view.findViewById(R.id.leaveStarFifth);
        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSubmit(actualReviewText.getText().toString());
            }
        });
        starOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClicked(1);
            }
        });
        starTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClicked(2);
            }
        });
        starThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClicked(3);
            }
        });
        starFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClicked(4);
            }
        });
        starFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClicked(5);
            }
        });
        return view;
    }
    public void startSubmit(final String review) {
        if (TextUtils.isEmpty(review)) {
            Toast.makeText(getActivity(), "Review Text Can't be Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(oldRated!=-1)
        {
            listner.dismissMe(frag);
            listner.makeLoadingSnackBar("Please Wait");
            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                            .collection("rating").document("rating_rev")
                            .collection(String.valueOf(oldRated)).document(userId)
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            doRating(review,rating);
                        }
                    });
                }
            });
        }
        else
        {
            doRating(review,rating);
        }
        /* Simple Method
        try {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(final DocumentSnapshot documentSnapshot) {
                            listner.addedReview(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                            db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                    .collection("reviews").document(userId)
                                    .set(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating)).addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("institute_list").document(
                                                    Objects.requireNonNull(documentSnapshot.getString("dept"))).collection("reviews").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                                                            db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot1) {
                                                                    double OldRating = Objects.requireNonNull(documentSnapshot1.getDouble("collegeRating"));
                                                                    double OldCount = Objects.requireNonNull(documentSnapshot1.getDouble("collegeReview"));
                                                                    double count = 0;
                                                                    for (DocumentSnapshot ignored : queryDocumentSnapshots) {
                                                                        count++;
                                                                    }
                                                                    double newRating;
                                                                    if (oldRated == -1 && count != 0)
                                                                        newRating = ((OldRating * OldCount) + rating) / count;
                                                                    else if (count != 1)
                                                                        newRating = (((OldRating * OldCount) + rating) - oldRated) / count;
                                                                    else
                                                                        newRating = rating;
                                                                    if (newRating > 5) {
                                                                        newRating = ThreadLocalRandom.current().nextDouble(4.50, 5.00);
                                                                    }
                                                                    DecimalFormat df2 = new DecimalFormat("#.#");
                                                                    newRating = Double.valueOf(df2.format(newRating));
                                                                    db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept"))).update("collegeReview", count
                                                                            , "collegeRating", newRating);
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }
                            );
                            listner.dismissMe(frag);
                            listner.makeSnackB("Review Submitted!");
                        }
                    });
        }catch (Exception e)
        {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        */
    }
    public void doRating(final String review,final int rating)
    {
        listner.dismissMe(frag);
        listner.makeLoadingSnackBar("Please Wait");
        switch(rating)
        {
            case 1:
                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listner.addedReview(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("uid",userId);
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("reviews").document(userId)
                                .set(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("rating").document("rating_rev")
                                .collection("1").document(userId)
                                .set(myMap);
                        listner.makeSnackB("Review Submitted!");
                    }
                });
                break;
            case 2:
                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("uid",userId);
                        listner.addedReview(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("reviews").document(userId)
                                .set(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("rating").document("rating_rev")
                                .collection("2").document(userId)
                                .set(myMap);
                        listner.dismissMe(frag);
                        listner.makeSnackB("Review Submitted!");
                    }
                });
                break;
            case 3:
                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("uid",userId);
                        listner.addedReview(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("reviews").document(userId)
                                .set(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("rating").document("rating_rev")
                                .collection("3").document(userId)
                                .set(myMap);
                        listner.dismissMe(frag);
                        listner.makeSnackB("Review Submitted!");
                    }
                });
                break;
            case 4:
                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("uid",userId);
                        listner.addedReview(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("reviews").document(userId)
                                .set(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("rating").document("rating_rev")
                                .collection("4").document(userId)
                                .set(myMap);
                        listner.dismissMe(frag);
                        listner.makeSnackB("Review Submitted!");
                    }
                });
                break;
            case 5:
                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("uid",userId);
                        listner.addedReview(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("reviews").document(userId)
                                .set(new ReviewModel(review, documentSnapshot.getString("rno"), userId, rating));
                        db.collection("institute_list").document(Objects.requireNonNull(documentSnapshot.getString("dept")))
                                .collection("rating").document("rating_rev")
                                .collection("5").document(userId)
                                .set(myMap);
                        listner.dismissMe(frag);
                        listner.makeSnackB("Review Submitted!");
                    }
                });
                break;
        }
    }
    public void saveToMain()
    {

    }
    /*Something Stupid for now (Don't Look)*/
    public void startClicked(int num)
    {
        rating = num;
        switch (num)
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
}
