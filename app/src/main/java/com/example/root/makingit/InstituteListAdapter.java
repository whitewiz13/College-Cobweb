package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.List;

public class InstituteListAdapter extends RecyclerView.Adapter<InstituteListAdapter.MyViewHolder> {

    private Context mContext;
    private List<CollegeInfo> collegeList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reviewRef = db.collection("institute_list");
    public InstituteListAdapter(List<CollegeInfo> collegeList, Context mContext) {
        this.collegeList = collegeList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_institute_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final CollegeInfo collegeInfo = collegeList.get(position);
        holder.collegeName.setText(collegeInfo.getCollegeName());
        holder.collegeDetail.setText(collegeInfo.getCollegeAbout());
        holder.collegeAddress.setText(collegeInfo.getCollegeAddress());
        if(collegeInfo.getCollegeImage() != null)
        {
            GlideApp.with(mContext)
                    .load(collegeInfo.getCollegeImage())
                    .placeholder(R.drawable.loadme)
                    .into(holder.collegeImage);
            holder.collegeImage.setVisibility(View.VISIBLE);
        }
        else {
            holder.collegeImage.setVisibility(View.GONE);
        }
        holder.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.collegeDetail.getMaxLines()==3)
                    holder.collegeDetail.setMaxLines(200);
                else
                    holder.collegeDetail.setMaxLines(3);
            }
        });
        holder.exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext,InstituteMainActivity.class);
                i.putExtra("collegeId",collegeInfo.getCollegeId());
                mContext.startActivity(i);
            }
        });
        loadRating(holder,collegeInfo);
    }
    public void loadRating(final MyViewHolder holder, final CollegeInfo collegeInfo)
    {
        final String dept = collegeInfo.getCollegeId();
        holder.insProgress.setVisibility(View.VISIBLE);
        reviewRef.document(dept).collection("rating")
                .document("rating_rev").collection("5")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ignored : queryDocumentSnapshots)
                {
                    holder.fiveStar++;
                }
                reviewRef.document(dept).collection("rating")
                        .document("rating_rev").collection("4")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ignored : queryDocumentSnapshots)
                        {
                            holder.fourStar++;
                        }
                        reviewRef.document(dept).collection("rating")
                                .document("rating_rev").collection("3")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot ignored : queryDocumentSnapshots)
                                {
                                    holder.threeStar++;
                                }
                                reviewRef.document(dept).collection("rating")
                                        .document("rating_rev").collection("2")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(DocumentSnapshot ignored : queryDocumentSnapshots)
                                        {
                                            holder.twoStar++;
                                        }
                                        reviewRef.document(dept).collection("rating")
                                                .document("rating_rev").collection("1")
                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for(DocumentSnapshot ignored : queryDocumentSnapshots)
                                                {
                                                    holder.oneStar++;
                                                }
                                                try {
                                                    double tt = 5 * holder.fiveStar + 4 * holder.fourStar + 3 * holder.threeStar + 2 * holder.twoStar
                                                            + holder.oneStar;
                                                    double ts = holder.fiveStar + holder.fourStar + holder.threeStar + holder.twoStar + holder.oneStar;
                                                    double tr = tt/ ts;
                                                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                                    String numberAsString = decimalFormat.format(tr);
                                                    holder.realStarNum.setText(numberAsString);
                                                    holder.numUsers.setText(String.valueOf(((int)ts)));
                                                    setUpStars(holder, (int)tr);
                                                }catch (Exception e)
                                                {

                                                }
                                                holder.insProgress.setVisibility(View.GONE);
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
    public void setUpStars(MyViewHolder holder, int num)
    {
        switch (num)
        {
            case 5:
                holder.starFive.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starFour.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starThree.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starTwo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starOne.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                break;
            case 4:
                holder.starFive.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starFour.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starThree.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starTwo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starOne.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                break;
            case 3:
                holder.starFive.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starFour.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starThree.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starTwo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starOne.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                break;
            case 2:
                holder.starFive.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starFour.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starThree.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starTwo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                holder.starOne.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                break;
            case 1:
                holder.starFive.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starFour.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starThree.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starTwo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                holder.starOne.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGold)));
                break;
        }
    }
    @Override
    public int getItemCount() {
        return collegeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        int fiveStar=0,fourStar=0,threeStar=0,twoStar=0,oneStar=0;
        ProgressBar insProgress;
        TextView collegeName,collegeDetail,collegeAddress,showMore,realStarNum,numUsers;
        ImageButton starOne,starTwo,starThree,starFour,starFive;
        ImageView collegeImage;
        Button exploreButton;
        public MyViewHolder(View view){
            super(view);
            insProgress = view.findViewById(R.id.insProgress);
            numUsers = view.findViewById(R.id.instituteNumberOfUsers);
            realStarNum = view.findViewById(R.id.instituteRatingText);
            starOne = view.findViewById(R.id.starFirst);
            starTwo = view.findViewById(R.id.starSecond);
            starThree= view.findViewById(R.id.starThird);
            starFour = view.findViewById(R.id.starFourth);
            starFive = view.findViewById(R.id.starFifth);
            collegeName = view.findViewById(R.id.collegeName);
            collegeDetail = view.findViewById(R.id.collegeDetail);
            collegeAddress = view.findViewById(R.id.locationText);
            collegeImage = view.findViewById(R.id.instituteImageView);
            showMore = view.findViewById(R.id.instituteShowMore);
            exploreButton = view.findViewById(R.id.instituteExplore);
        }
    }


}
