package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class ForumPostAdapter extends FirestoreRecyclerAdapter<ForumPostInfo,ForumPostAdapter.MyViewHolder> {

    private OnActionListener mListener;
    private Context mContext;
    public FirebaseAuth auth = FirebaseAuth.getInstance();;
    public FirebaseFirestore db= FirebaseFirestore.getInstance();
    public ForumPostAdapter(@NonNull FirestoreRecyclerOptions<ForumPostInfo> options,Context mContext, OnActionListener mListener) {
        super(options);
        this.mListener = mListener;
        this.mContext = mContext;
    }
    interface OnActionListener{
        void showSnackBar(String msg);
    }
    @Override
    protected void onBindViewHolder(@NonNull final ForumPostAdapter.MyViewHolder holder, int position, @NonNull final ForumPostInfo model) {
        loadAuthorInfo(holder,model);
        setUpDateAndTime(model.getFdate(),holder);
        setUpCommentButton(holder,model.getFid());
        doDeleteButton(holder,model);
        doUpVoteButton(holder,model);
        checkForUserPost(model,holder);
        holder.fname.setText(model.getFname());
        holder.fdetail.setText(model.getFdetail());
        holder.fcomments.setText(model.getFcomment());
        holder.fupvotes.setText(model.getFupvote());
        holder.upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAlreadyUpvoted(model,holder);
            }
        });
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
            holder.fdate.setText("Just now");
        else if(minutes < 60)
            holder.fdate.setText(String.valueOf(minutes).concat("m"));
        else if(hours<24)
            holder.fdate.setText(String.valueOf(hours).concat("h"));
        else if(days<=31)
            holder.fdate.setText(String.valueOf(days).concat("d"));
        else if(months<12)
            holder.fdate.setText(String.valueOf(months).concat("mon"));
        else
            holder.fdate.setText(String.valueOf(years).concat("yrs"));
    }
    public void loadAuthorInfo(final MyViewHolder holder, ForumPostInfo model)
    {
        db.collection("users").document(model.getFauthor()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null) {
                    holder.authorName.setText(documentSnapshot.getString("name"));
                    holder.authorRno.setText(documentSnapshot.getString("rno"));
                    GlideApp.with(mContext)
                            .load(documentSnapshot.getString("uimage"))
                            .placeholder(R.drawable.loadme)
                            .into(holder.forumProfilePic);
                }
            }
        });
    }
    public void trackUpVotes(final ForumPostInfo model)
    {
        db.collection("forum_posts").document(model.getFid()).collection("upvotes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot ignored : Objects.requireNonNull(task.getResult())) {
                                count++;
                            }
                            HashMap<String,Object> newData = new HashMap<>();
                            newData.put("fupvote", String.valueOf(count));
                            DocumentReference docRef = db.collection("forum_posts").document(Objects.requireNonNull(model.getFid()));
                            docRef.update(newData);
                        }
                    }
                });
    }
    public void doUpVoteButton(final MyViewHolder holder, final ForumPostInfo model)
    {
        if(auth.getCurrentUser()!=null) {
            db.collection("forum_posts").document(model.getFid()).collection("upvotes").document(auth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap != null)
                        if (docSnap.exists()) {
                            holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                        } else {
                            holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                        }
                }
            });
        }
    }
    public void checkAlreadyUpvoted(final ForumPostInfo model,final MyViewHolder holder)
    {
        db.collection("forum_posts").document(model.getFid()).collection("upvotes").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if(document.exists()) {
                            mListener.showSnackBar("You removed your vote!");
                            db.collection("forum_posts").document(model.getFid()).collection("upvotes").document(auth.getCurrentUser().getUid()).delete();
                            trackUpVotes(model);
                        }
                        else {
                            mListener.showSnackBar("You upvoted this post!");
                            Map<String,Object> myMap = new HashMap<>();
                            myMap.put("more_stuff","blank");
                            db.collection("forum_posts").document(model.getFid()).collection("upvotes").document(auth.getCurrentUser().getUid()).set(myMap);
                            trackUpVotes(model);
                        }
                        doUpVoteButton(holder,model);
                    }
                });
    }
    public void checkForUserPost(ForumPostInfo model,MyViewHolder holder)
    {
        if(Objects.requireNonNull(auth.getCurrentUser()).getUid().equals(model.getFauthor()) && model.getFauthor()!=null)
        { holder.deletePost.setVisibility(View.VISIBLE); }
        else
        { holder.deletePost.setVisibility(View.GONE); }
    }
    public void doDeleteButton(final MyViewHolder holder, final ForumPostInfo album) {
        holder.deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("forum_posts").document(album.getFid())
                        .delete();

            }
        });
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fname,fdetail,fdate,fupvotes,fcomments,commentButton,authorName,authorRno;
        CircleImageView forumProfilePic;
        public Button deletePost,upVote;
        public MyViewHolder(View view) {
            super(view);
            forumProfilePic = view.findViewById(R.id.forumPostUserProfile);
            upVote = view.findViewById(R.id.upVoteButton);
            deletePost = view.findViewById(R.id.deletePost);
            commentButton = view.findViewById(R.id.commentButton);
            fname = view.findViewById(R.id.fname);
            fdetail = view.findViewById(R.id.fdetail);
            fdate = view.findViewById(R.id.fdate);
            fupvotes = view.findViewById(R.id.fUpvotes);
            fcomments = view.findViewById(R.id.fcomments);
            authorName = view.findViewById(R.id.forumAuthor);
            authorRno=view.findViewById(R.id.authorRno);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forum_post_card, parent, false);
        return new MyViewHolder(itemView);
    }
    private void setUpCommentButton(MyViewHolder holder, final String fPostId)
    {
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext,ForumPostCommentActivity.class);
                i.putExtra("postId",fPostId);
                mContext.startActivity(i);
            }
        });
    }
}
