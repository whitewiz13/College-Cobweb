package com.example.root.makingit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPostAdapter extends RecyclerView.Adapter<CommentPostAdapter.MyViewHolder> {

    private Context mContext;
    private String checkId;
    private String fauthoerId;
    List<CommentPostInfo> commentList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    private CollectionReference colref =db.collection("users");
    public CommentPostAdapter(List<CommentPostInfo> commentList,Context mContext,String checkId) {
        this.checkId = checkId;
        this.mContext = mContext;
        this.commentList = commentList;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.upvote.setEnabled(false);
        final CommentPostInfo model = commentList.get(position);
        loadUpvoteCommentData(model,holder);
        holder.commentDetail.setText(model.getCommenttext());
        holder.commentUpvotes.setText(model.getUpvotes());
        holder.commentDownvotes.setText(model.getDownvotes());
        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!model.getUpvoted())
                {
                    model.setCount(Integer.parseInt(holder.commentUpvotes.getText().toString()));
                    holder.upvote.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                    holder.commentUpvotes.setTextColor(ColorStateList.valueOf(Color.BLUE));
                    model.setUpvoted(true);
                    model.setCount(model.getCount()+1);
                    model.setUpvotes(String.valueOf(model.getCount()));
                    holder.commentUpvotes.setText(String.valueOf(model.getCount()));
                    writeToDb(model,model.getUpvoted());
                }
                else
                {
                    model.setCount(Integer.parseInt(holder.commentUpvotes.getText().toString()));
                    holder.upvote.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                    holder.commentUpvotes.setTextColor(ColorStateList.valueOf(Color.GRAY));
                    model.setUpvoted(false);
                    model.setCount(model.getCount()-1);
                    model.setUpvotes(String.valueOf(model.getCount()));
                    holder.commentUpvotes.setText(String.valueOf(model.getCount()));
                    writeToDb(model,model.getUpvoted());
                }
            }
        });
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        setUpDateAndTime(model.getCommenttime(),holder);
        getUserInfo(model.getCommenterid(),holder);
    }
    public void writeToDb(final CommentPostInfo model,Boolean added)
    {
        if(added) {
            Map<String, Object> myMap = new HashMap<>();
            myMap.put("more_stuff", "blank");
            db.collection("forum_posts").document(checkId).collection("comments")
                    .document(model.getCommentid()).collection("upvotes")
                    .document(uid)
                    .set(myMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    countUpvotes(model);
                }
            });
        }
        else
        {
            db.collection("forum_posts").document(checkId).collection("comments")
                    .document(model.getCommentid()).collection("upvotes")
                    .document(uid)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    countUpvotes(model);
                }
            });
        }
    }
    public void countUpvotes(final CommentPostInfo model)
    {
        db.collection("forum_posts").document(checkId).collection("comments")
                .document(model.getCommentid()).collection("upvotes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (DocumentSnapshot ignored : queryDocumentSnapshots) {
                            count++;
                        }
                        model.setCount(count);
                        db.collection("forum_posts").document(checkId)
                                .collection("comments")
                                .document(model.getCommentid()).update("upvotes",String.valueOf(count));
                    }
                });
    }
    public void loadUpvoteCommentData(final CommentPostInfo model, final MyViewHolder holder)
    {
        db.collection("forum_posts").document(checkId).collection("comments")
                .document(model.getCommentid()).collection("upvotes")
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            if (documentSnapshot.exists()) {
                                model.setUpvoted(true);
                                holder.upvote.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                                holder.commentUpvotes.setTextColor(ColorStateList.valueOf(Color.BLUE));
                                holder.upvote.setEnabled(true);
                            } else {
                                model.setUpvoted(false);
                                holder.commentUpvotes.setTextColor(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                                holder.upvote.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                                holder.upvote.setEnabled(true);
                            }
                        }
                    }
                });
    }
    @Override
    public int getItemCount() {
        return commentList.size();
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
                        holder.commentAuthor.setText(Html.fromHtml(Objects.requireNonNull(documentSnapshot.getString("rno").concat(" ( ").concat(documentSnapshot.getString("name")).concat(" )")).concat(" <font color=\"#7C0A02\"><bold> (OP)</bold></font>")), TextView.BufferType.SPANNABLE);
                    }
                    else
                    { holder.commentAuthor.setText(documentSnapshot.getString("rno").concat(" ( ").concat(documentSnapshot.getString("name")).concat(" )"));}
                    GlideApp.with(mContext)
                            .load(documentSnapshot.getString("uimage"))
                            .placeholder(R.drawable.loadme)
                            .into(holder.commentProfilePic);
                }
            }
        });
    }
    @Override
    public long getItemId(int position) {
        return commentList.get(position).hashCode();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_post_card, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView commentDetail,commentAuthor,commentTime,commentUpvotes,commentDownvotes;
        ImageButton upvote,downvote,reply;
        CircleImageView commentProfilePic;
        public MyViewHolder(View view)
        {
            super(view);
            commentDetail = view.findViewById(R.id.commentDetail);
            commentUpvotes = view.findViewById(R.id.commentUpvotes);
            commentDownvotes = view.findViewById(R.id.commentDownvotes);
            upvote = view.findViewById(R.id.upVoteComment);
            downvote = view.findViewById(R.id.downVoteComment);
            reply = view.findViewById(R.id.replyToComment);
            commentAuthor = view.findViewById(R.id.commentAuthor);
            commentTime = view.findViewById(R.id.commentTime);
            commentProfilePic = view.findViewById(R.id.commentProfilePic);
        }
    }
}