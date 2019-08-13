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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.MyViewHolder> {

    private OnActionListener mListener;
    private Context mContext;
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore db= FirebaseFirestore.getInstance();
    List<ForumPostInfo> forumList;
    public ForumPostAdapter(List<ForumPostInfo> forumList,Context mContext, OnActionListener mListener) {
        this.forumList = forumList;
        this.mListener = mListener;
        this.mContext = mContext;
    }
    interface OnActionListener{
        void showSnackBar(String msg);
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    @Override
    public void onBindViewHolder(@NonNull final ForumPostAdapter.MyViewHolder holder, int position) {
        final ForumPostInfo model = forumList.get(position);
        loadAuthorInfo(holder,model);
        setUpDateAndTime(model.getFdate(),holder);
        setUpCommentButton(holder,model.getFid());
        doDeleteButton(holder,model,position);
        doUpVoteButton(holder,model);
        checkForUserPost(model,holder);
        holder.fname.setText(model.getFname());
        holder.fdetail.setText(model.getFdetail());
        holder.fcomments.setText(model.getFcomment());
        holder.fupvotes.setText(model.getFupvote());
        if(model.getForumImage() != null)
        {
            GlideApp.with(mContext)
                    .load(model.getForumImage())
                    .placeholder(R.drawable.loadme)
                    .into(holder.forumImageView);
            holder.forumImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.forumImageView.setVisibility(View.GONE);
        }
        holder.upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAlreadyUpvoted(model,holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumList.size();
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
    public void trackUpVotes(final ForumPostInfo model,final MyViewHolder holder)
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
                            holder.fupvotes.setText(String.valueOf(count));
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
                    try {
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null)
                            if (docSnap.exists()) {
                                holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                            } else {
                                holder.upVote.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                            }
                    }catch (Exception e)
                    {
                        Toast.makeText(mContext,"Turn on network connection!",Toast.LENGTH_SHORT).show();
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
                        try {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                mListener.showSnackBar("You removed your vote!");
                                db.collection("forum_posts").document(model.getFid()).collection("upvotes").document(auth.getCurrentUser().getUid()).delete();
                                trackUpVotes(model, holder);
                            } else {
                                mListener.showSnackBar("You upvoted this post!");
                                Map<String, Object> myMap = new HashMap<>();
                                myMap.put("more_stuff", "blank");
                                db.collection("forum_posts").document(model.getFid()).collection("upvotes").document(auth.getCurrentUser().getUid()).set(myMap);
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
    public void checkForUserPost(ForumPostInfo model,MyViewHolder holder)
    {
        if(Objects.requireNonNull(auth.getCurrentUser()).getUid().equals(model.getFauthor()) && model.getFauthor()!=null)
        { holder.deletePost.setVisibility(View.VISIBLE); }
        else
        { holder.deletePost.setVisibility(View.GONE); }
    }
    public void doDeleteButton(final MyViewHolder holder, final ForumPostInfo album,final int position) {
        holder.deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (album.getForumImage() != null) {
                    mListener.makeLoadingSnackBar("Deleting Event...");
                    FirebaseStorage.getInstance().getReferenceFromUrl(album.getForumImage()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("forum_posts").document(album.getFid())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mListener.dismissSnackBar();
                                                    mListener.showSnackBar("Successfully Deleted!");
                                                    forumList.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, forumList.size());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                }
                            });
                } else {
                    db.collection("forum_posts").document(album.getFid())
                            .delete();
                    forumList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, forumList.size());
                    mListener.showSnackBar("Post deleted!");
                }
            }
        });
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fname,fdetail,fdate,fupvotes,fcomments,commentButton,authorName,authorRno;
        CircleImageView forumProfilePic;
        ImageView forumImageView;
        public Button deletePost,upVote;
        public MyViewHolder(View view) {
            super(view);
            forumImageView = view.findViewById(R.id.forumImageView);
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
