package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPostAdapter extends FirestoreRecyclerAdapter<CommentPostInfo, CommentPostAdapter.MyViewHolder> {

    private Context mContext;
    private String checkId;
    private String fauthoerId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colref =db.collection("users");
    public CommentPostAdapter(@NonNull FirestoreRecyclerOptions<CommentPostInfo> options,Context mContext,String checkId) {
        super(options);
        this.checkId = checkId;
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
            holder.commentTime.setText("Just now");
        else if(minutes < 60)
            holder.commentTime.setText(String.valueOf(minutes).concat(" minutes ago"));
        else if(hours<24)
            holder.commentTime.setText(String.valueOf(hours).concat(" hours ago"));
        else if(days<=31)
            holder.commentTime.setText(String.valueOf(days).concat(" days ago"));
        else if(months<12)
            holder.commentTime.setText(String.valueOf(months).concat(" months ago"));
        else
            holder.commentTime.setText(String.valueOf(years).concat(" years ago"));
    }

    public void getUserInfo(final String authorId, final MyViewHolder holder)
    {
        db.collection("forum_posts").document(checkId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null) {
                    fauthoerId = documentSnapshot.getString("fauthor");
                    }
                }
            });
        colref.document(authorId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if(fauthoerId.equals(authorId))
                    {
                        holder.commentAuthor.setText(Html.fromHtml(Objects.requireNonNull(documentSnapshot.getString("name")).concat(" <font color=\"#7C0A02\"><bold> (OP)</bold></font>")), TextView.BufferType.SPANNABLE);
                    }
                    else
                    { holder.commentAuthor.setText(documentSnapshot.getString("name"));}
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
