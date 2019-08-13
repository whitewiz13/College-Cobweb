package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InstituteListAdapter extends RecyclerView.Adapter<InstituteListAdapter.MyViewHolder> {

    private Context mContext;
    private List<CollegeInfo> collegeList;
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
        holder.collegeRating.setText(collegeInfo.getCollegeRating());
        holder.collegeAddress.setText(collegeInfo.getCollegeAddress());
        holder.popularCourse.setText(collegeInfo.getPopularCourses());
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
    }
    @Override
    public int getItemCount() {
        return collegeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView collegeName,collegeDetail,collegeRating,collegeAddress,popularCourse,showMore;
        ImageView collegeImage;
        Button exploreButton;
        public MyViewHolder(View view){
            super(view);
            collegeName = view.findViewById(R.id.collegeName);
            collegeDetail = view.findViewById(R.id.collegeDetail);
            collegeRating = view.findViewById(R.id.ratingText);
            collegeAddress = view.findViewById(R.id.locationText);
            popularCourse = view.findViewById(R.id.popularCourseText);
            collegeImage = view.findViewById(R.id.instituteImageView);
            showMore = view.findViewById(R.id.instituteShowMore);
            exploreButton = view.findViewById(R.id.instituteExplore);
        }
    }


}
