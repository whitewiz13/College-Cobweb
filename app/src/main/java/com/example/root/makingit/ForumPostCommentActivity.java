package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class ForumPostCommentActivity extends AppCompatActivity {

    ImageView forumPostImage;
    Snackbar loadingSnack;
    FloatingActionButton postComment;
    EditText commentTypeBox;
    String id;
    TextView postDetail,postUpvotes,postComments;
    Toolbar tb;
    ActionBar ab;
    public String authUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    final public FirebaseFirestore db= FirebaseFirestore.getInstance();
    ForumPostInfo model;
    RecyclerView postCommentRec;
    CommentPostAdapter adapter;
    Query colRef;
    List<CommentPostInfo> commentList = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_post_comment_activity);
        postComment = findViewById(R.id.postCommentButton);
        postComments =findViewById(R.id.postComments);
        forumPostImage = findViewById(R.id.forumPostMainImage);
        postUpvotes = findViewById(R.id.postUpvotes);
        commentTypeBox = findViewById(R.id.commentTypeBox);
        postDetail = findViewById(R.id.postDetail);
        tb = findViewById(R.id.forumPostToolbar);
        postCommentRec = findViewById(R.id.postCommentsSection);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!commentTypeBox.getText().toString().equals("")) {
                    uploadAndSaveComment(commentTypeBox.getText().toString(), id);
                    commentTypeBox.setText("");
                }
            }
        });
        setSupportActionBar(tb);
        ab = getSupportActionBar();
        tb.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        if(ab!=null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("postId");
            db.collection("forum_posts").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot!=null) {
                        model = documentSnapshot.toObject(ForumPostInfo.class);
                        if(model!=null) {
                            postUpvotes.setText(model.getFupvote());
                            postDetail.setText(model.getFdetail());
                            postComments.setText(model.getFcomment());
                            ab.setTitle(model.getFname());
                            if(model.getForumImage() != null)
                            {
                                GlideApp.with(getApplicationContext())
                                        .load(model.getForumImage())
                                        .placeholder(R.drawable.loadme)
                                        .into(forumPostImage);
                                forumPostImage.setVisibility(View.VISIBLE);
                            }
                            else {
                                forumPostImage.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
            loadCommentList();
        }
    }
    public void loadCommentList()
    {
        makeLoadingSnackBar("Loading Comments...");
        commentList.clear();
        colRef = db.collection("forum_posts").document(id).collection("comments")
                .orderBy("commenttime",Query.Direction.DESCENDING);
        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    commentList.add(documentSnapshot.toObject(CommentPostInfo.class)); }
                adapter = new CommentPostAdapter(commentList,getApplicationContext(),id);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ForumPostCommentActivity.this,1)
                {
                    @Override
                    public boolean supportsPredictiveItemAnimations()
                    {
                        return true;
                    }
                };
                adapter.setHasStableIds(true);
                postCommentRec.setLayoutManager(gridLayoutManager);
                postCommentRec.setAdapter(adapter);
                dismissSnackBar();
            }
        });
    }
    public void uploadAndSaveComment(String commentText,final String id)
    {
        final CommentPostInfo postObject = new CommentPostInfo(authUid,commentText,"0","0","0");
        db.collection("forum_posts").document(id).collection("comments")
                .add(postObject).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String commentID = Objects.requireNonNull(task.getResult()).getId();
                db.collection("forum_posts").document(id).collection("comments")
                        .document(commentID).update("commentid",commentID);
                postObject.setCommentid(commentID);
                commentList.add(0,postObject);
                adapter.notifyDataSetChanged();
                trackComments(model,postComments);
            }
        });
    }
    public void trackComments(final ForumPostInfo model, final TextView commentText)
    {
        db.collection("forum_posts").document(model.getFid()).collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot ignored : Objects.requireNonNull(task.getResult())) {
                                count++;
                            }
                            commentText.setText(String.valueOf(count));
                            HashMap<String,Object> newData = new HashMap<>();
                            newData.put("fcomment", String.valueOf(count));
                            DocumentReference docRef = db.collection("forum_posts").document(Objects.requireNonNull(model.getFid()));
                            docRef.update(newData);
                        }
                    }
                });
    }
    public void makeLoadingSnackBar(String msg) {
            loadingSnack = Snackbar.make(findViewById(R.id.commentPostScreen), msg, Snackbar.LENGTH_INDEFINITE);
            loadingSnack.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void dismissSnackBar() {
            loadingSnack.dismiss();
    }
}