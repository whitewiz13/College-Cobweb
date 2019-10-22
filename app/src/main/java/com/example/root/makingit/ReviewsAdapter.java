package com.example.root.makingit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    List<ReviewModel> reviewList;
    Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ReviewsAdapter(List<ReviewModel> reviewList,Context mContext)
    {
        this.mContext = mContext;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_info_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ReviewModel reviewModel = reviewList.get(position);
        holder.reviewText.setText(reviewModel.getReviewText());
        holder.userName.setText(reviewModel.getAuthorName());
        setUpStars(holder,reviewModel.getRating());
        db.collection("users").document(reviewModel.authorRno)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    GlideApp.with(mContext)
                            .load(documentSnapshot.getString("uimage"))
                            .placeholder(R.drawable.loadme)
                            .into(holder.profileImage);
                }catch (Exception e)
                {
                    //
                }
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
        return reviewList.size();
    }
    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView reviewText,userName;
        CircleImageView profileImage;
        ImageButton starOne,starTwo,starThree,starFour,starFive;
        public MyViewHolder(View itemView) {
            super(itemView);
            starOne = itemView.findViewById(R.id.RStarFirst);
            starTwo = itemView.findViewById(R.id.RStarSecond);
            starThree = itemView.findViewById(R.id.RStarThird);
            starFour = itemView.findViewById(R.id.RStarFourth);
            starFive = itemView.findViewById(R.id.RStarFifth);
            profileImage = itemView.findViewById(R.id.userProfilePic);
            reviewText = itemView.findViewById(R.id.reviewText);
            userName = itemView.findViewById(R.id.reviewAuthor);
        }
    }
}