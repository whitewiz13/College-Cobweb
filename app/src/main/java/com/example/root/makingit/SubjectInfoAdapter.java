package com.example.root.makingit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SubjectInfoAdapter extends FirestoreRecyclerAdapter<SubjectInfoModel,SubjectInfoAdapter.MyViewHolder> {

    public SubjectInfoAdapter(@NonNull FirestoreRecyclerOptions<SubjectInfoModel> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SubjectInfoModel model) {
        holder.subcode.setText(model.getsubcode());
        holder.subname.setText(model.getsubname());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_name_card, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subname,subcode;
        public MyViewHolder(View view) {
            super(view);
            subcode = view.findViewById(R.id.subCode);
            subname = view.findViewById(R.id.subName);
        }
    }
}
