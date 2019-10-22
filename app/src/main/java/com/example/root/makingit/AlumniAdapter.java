package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class AlumniAdapter extends RecyclerView.Adapter<AlumniAdapter.MyViewHolder> {
    List<AlumniModel> alumniList;
    Context mContext;
    AlumniAdapter(List<AlumniModel> alumniList, Context mContext)
    {
        this.mContext = mContext;
        this.alumniList = alumniList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alumni_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AlumniModel alumniModel = alumniList.get(position);
        holder.alumniAuthor.setText(alumniModel.getAlumniName());
        holder.alumniText.setText(alumniModel.getAlumniText());
        holder.firstAch.setText(alumniModel.getFirstAch());
        holder.firstAchYear.setText(alumniModel.getFirstAchYear());
        holder.secondAch.setText(alumniModel.getSecondAch());
        holder.secondAchYear.setText(alumniModel.getSecondAchYear());
    }

    @Override
    public int getItemCount() {
        return alumniList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView alumniAuthor,alumniText,firstAch,firstAchYear,secondAch,secondAchYear;
        public MyViewHolder(View itemView) {
            super(itemView);
            alumniAuthor = itemView.findViewById(R.id.alumniAuthor);
            alumniText = itemView.findViewById(R.id.alumniText);
            firstAch = itemView.findViewById(R.id.firstAch);
            firstAchYear = itemView.findViewById(R.id.firstAchYear);
            secondAch = itemView.findViewById(R.id.secondAch);
            secondAchYear = itemView.findViewById(R.id.secondAchYear);
        }
    }
}
