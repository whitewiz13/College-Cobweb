package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPostAdapter extends FirestoreRecyclerAdapter<CommentPostInfo, CommentPostAdapter.MyViewHolder> {

    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colref =db.collection("users");
    public CommentPostAdapter(@NonNull FirestoreRecyclerOptions<CommentPostInfo> options,Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull CommentPostInfo model) {
        holder.commentDetail.setText(model.getCommenttext());
        setUpDateAndTime(model.getCommenttime(),holder);
        getUserInfo(model.getCommenterid(),holder);
    }

    public void setUpDateAndTime(Date date,MyViewHolder holder)
    {
        Date nowDate = new Date();
        long timeInMilliSeconds = 0;
        if(date!=null)
            timeInMilliSeconds = nowDate.getTime()-date.getTime();
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days/30;
        long years = days/365;
        if(seconds<60)
            holder.commentTime.setText(Math.abs(seconds)+" seconds ago");
        else if(minutes < 60)
            holder.commentTime.setText(minutes+" minutes ago");
        else if(hours<24)
            holder.commentTime.setText(hours+" hours ago");
        else if(days<=31)
            holder.commentTime.setText(days+" days ago");
        else if(months<12)
            holder.commentTime.setText(months+" months ago");
        else
            holder.commentTime.setText(years + " years ago");
    }

    public void getUserInfo(String authorId,final MyViewHolder holder)
    {
        colref.document(authorId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null)
                    holder.commentAuthor.setText(documentSnapshot.getString("name"));
                if (documentSnapshot != null) {
                    GlideApp.with(mContext)
                            .load(documentSnapshot.getString("uimage"))
                            .placeholder(R.drawable.loadme)
                            .into(holder.commentProfilePic);
                }
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_post_card, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView commentDetail,commentAuthor,commentTime;
        CircleImageView commentProfilePic;
        public MyViewHolder(View view)
        {
            super(view);
            commentDetail = view.findViewById(R.id.commentDetail);
            commentAuthor = view.findViewById(R.id.commentAuthor);
            commentTime = view.findViewById(R.id.commentTime);
            commentProfilePic = view.findViewById(R.id.commentProfilePic);
        }
    }
}
