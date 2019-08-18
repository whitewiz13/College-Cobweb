package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CollegeSearchListAdapter extends RecyclerView.Adapter<CollegeSearchListAdapter.MyViewHolder> {
    private OnItemClickListener listener;
    List<CollegeListSearchModel> collegeSearchList;
    Context mContext;
    public CollegeSearchListAdapter(List<CollegeListSearchModel> collegeSearchList,Context mContext)
    {
        this.collegeSearchList = collegeSearchList;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_search_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CollegeListSearchModel collegeListSearchModel = collegeSearchList.get(position);
        holder.collegeName.setText(collegeListSearchModel.getCollegeName());
        holder.collegeAddress.setText(collegeListSearchModel.getCollegeAddress());

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView collegeName,collegeAddress;
        public MyViewHolder(View view) {
            super(view);
            collegeName = view.findViewById(R.id.collegeSearchName);
            collegeAddress = view.findViewById(R.id.collegeSearchLoc);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return collegeSearchList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
