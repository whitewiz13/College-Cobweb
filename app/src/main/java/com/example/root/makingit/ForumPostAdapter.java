package com.example.root.makingit;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
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
    String uid = auth.getCurrentUser().getUid();
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
        holder.upVote.setEnabled(false);
        final ForumPostInfo model = forumList.get(position);
        loadUpvoteCommentData(model,holder);
        loadAuthorInfo(holder,model);
        setUpDateAndTime(model.getFdate(),holder);
        setUpCommentButton(holder,model.getFid());
        doDeleteButton(holder,model,position);
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
                if(!model.getUpvoted())
                {
                    model.setCount(Integer.parseInt(holder.fupvotes.getText().toString()));
                    holder.upVote.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                    model.setUpvoted(true);
                    model.setCount(model.getCount()+1);
                    model.setFupvote(String.valueOf(model.getCount()));
                    holder.fupvotes.setText(String.valueOf(model.getCount()));
                    writeToDb(model,model.getUpvoted());
                }
                else
                {
                    model.setCount(Integer.parseInt(holder.fupvotes.getText().toString()));
                    holder.upVote.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                    model.setUpvoted(false);
                    model.setCount(model.getCount()-1);
                    model.setFupvote(String.valueOf(model.getCount()));
                    holder.fupvotes.setText(String.valueOf(model.getCount()));
                    writeToDb(model,model.getUpvoted());
                }
            }
        });
    }
    public void writeToDb(final ForumPostInfo model,Boolean added)
    {
        if(added) {
            Map<String, Object> myMap = new HashMap<>();
            myMap.put("more_stuff", "blank");
            db.collection("forum_posts").document(model.getFid()).collection("upvotes")
                    .document(uid).set(myMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    countUpvotes(model);
                }
            });
        }
        else
        {
            db.collection("forum_posts").document(model.getFid()).collection("upvotes")
                    .document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    countUpvotes(model);
                }
            });
        }
    }
    public void countUpvotes(final ForumPostInfo model)
    {

        db.collection("forum_posts").document(model.getFid()).collection("upvotes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (DocumentSnapshot ignored : queryDocumentSnapshots) {
                            count++;
                        }
                        model.setCount(count);
                        db.collection("forum_posts").document(model.getFid()).update("fupvote",String.valueOf(count));
                    }
                });
    }
    @Override
    public long getItemId(int position) {
        return forumList.get(position).hashCode();
    }
    public void loadUpvoteCommentData(final ForumPostInfo model, final MyViewHolder holder)
    {
        db.collection("forum_posts").document(model.getFid()).collection("upvotes")
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            if (documentSnapshot.exists()) {
                                model.setUpvoted(true);
                                holder.upVote.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                                holder.upVote.setEnabled(true);
                            } else {
                                model.setUpvoted(false);
                                holder.upVote.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                                holder.upVote.setEnabled(true);
                            }
                        }
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
                    final UserInfo model = documentSnapshot.toObject(UserInfo.class);
                    holder.authorName.setText(model.getName());
                    holder.authorRno.setText(model.getRno());
                    GlideApp.with(mContext)
                            .load(model.getUimage())
                            .placeholder(R.drawable.loadme)
                            .into(holder.forumProfilePic);
                    holder.forumProfilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(mContext, UserProfileOpenActivity.class);
                            i.putExtra("userId",model.getUid());
                            mContext.startActivity(i);
                        }
                    });
                }
            }
        });
    }
    public void checkForUserPost(ForumPostInfo model,MyViewHolder holder)
    {
        if(uid.equals(model.getFauthor()) && model.getFauthor()!=null)
        { holder.deletePost.setVisibility(View.VISIBLE); }
        else
        { holder.deletePost.setVisibility(View.GONE); }
    }
    public void doDeleteButton(final MyViewHolder holder, final ForumPostInfo album,final int position) {
        holder.deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext,holder.deletePost);
                popupMenu.getMenuInflater().inflate(R.menu.forum_popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.deletePost)
                            deletePost(album,position);
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    public void deletePost(final ForumPostInfo album,final int position)
    {
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
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    db.collection("forum_posts").document(album.getFid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mListener.dismissSnackBar();
                                    mListener.showSnackBar("Successfully Deleted!");
                                    forumList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
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
            notifyDataSetChanged();
            mListener.showSnackBar("Post deleted!");
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fname,fdetail,fdate,fupvotes,fcomments,authorName,authorRno,commentLable;
        CircleImageView forumProfilePic;
        ImageView forumImageView;
        public Button deletePost;
        ImageButton upVote,commentButton;
        public MyViewHolder(View view) {
            super(view);
            commentLable = view.findViewById(R.id.textView6);
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
