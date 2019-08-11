package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class InstituteListAdapter extends RecyclerView.Adapter<InstituteListAdapter.MyViewHolder> {

    Context mContext;
    List<CollegeInfo> collegeList;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CollegeInfo collegeInfo = collegeList.get(position);
        holder.collegeName.setText(collegeInfo.getCollegeName());
        holder.collegeDetail.setText(collegeInfo.getCollegeAbout());
    }

    @Override
    public int getItemCount() {
        return collegeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView collegeName,collegeDetail;
        public MyViewHolder(View view){
            super(view);
            collegeName = view.findViewById(R.id.collegeName);
            collegeDetail = view.findViewById(R.id.collegeDetail);
        }
    }

}
