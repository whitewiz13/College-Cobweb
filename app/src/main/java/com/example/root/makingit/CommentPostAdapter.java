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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPostAdapter extends FirestoreRecyclerAdapter<CommentPostInfo, CommentPostAdapter.MyViewHolder> {

    private Context mContext;
    private String checkId;
    private String fauthoerId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private CollectionReference colref =db.collection("users");
    public CommentPostAdapter(@NonNull FirestoreRecyclerOptions<CommentPostInfo> options,Context mContext,String checkId) {
        super(options);
        this.checkId = checkId;
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull final CommentPostInfo model) {
        holder.commentDetail.setText(model.getCommenttext());
        holder.commentUpvotes.setText(model.getUpvotes());
        holder.commentDownvotes.setText(model.getDownvotes());
        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUpDownComment(holder,model);
            }
        });
        setUpDateAndTime(model.getCommenttime(),holder);
        getUserInfo(model.getCommenterid(),holder);
        doUpVoteButton(holder,model);
    }
    void checkUpDownComment(final MyViewHolder holder,final CommentPostInfo model)
    {
        db.collection("forum_posts").document(checkId).collection("comments").document(model.getCommentid()).collection("upvotes")
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        db.collection("forum_posts").document(checkId).collection("comments").document(model.getCommentid())
                                .collection("upvotes").document(uid).delete();
                        trackUpVotes(model, holder);
                    } else {
                        Map<String, Object> myMap = new HashMap<>();
                        myMap.put("more_stuff", "blank");
                        db.collection("forum_posts").document(checkId).collection("comments").document(model.getCommentid())
                                .collection("upvotes").document(uid).set(myMap);
                        trackUpVotes(model, holder);
                    }
                    doUpVoteButton(holder, model);
                }catch (Exception e)
                {
                    Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void trackUpVotes(final CommentPostInfo model,final MyViewHolder holder)
    {
        db.collection("forum_posts").document(checkId).collection("comments").document(model.getCommentid())
                .collection("upvotes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot ignored : Objects.requireNonNull(task.getResult())) {
                                count++;
                            }
                            holder.commentUpvotes.setText(String.valueOf(count));
                            HashMap<String,Object> newData = new HashMap<>();
                            newData.put("upvotes", String.valueOf(count));
                            DocumentReference docRef = db.collection("forum_posts").document(checkId).collection("comments")
                                    .document(model.getCommentid());
                            docRef.update(newData);
                        }
                    }
                });
    }
    public void doUpVoteButton(final MyViewHolder holder, final CommentPostInfo model)
    {
        if(auth.getCurrentUser()!=null && model.getCommentid()!=null) {
            db.collection("forum_posts").document(checkId).collection("comments").document(model.getCommentid())
                    .collection("upvotes").document(uid)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    try {
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null)
                            if (docSnap.exists()) {
                                holder.upvote.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                            } else {
                                holder.upvote.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                            }
                    }catch (Exception e)
                    {
                        Toast.makeText(mContext,"Turn on network connection!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
