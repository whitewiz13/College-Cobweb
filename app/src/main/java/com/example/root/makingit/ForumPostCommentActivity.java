package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_post_comment_activity);
        makeLoadingSnackBar("Loading Comments...");
        postComment = findViewById(R.id.postCommentButton);
        postComments =findViewById(R.id.postComments);
        forumPostImage = findViewById(R.id.forumPostMainImage);
        postUpvotes = findViewById(R.id.postUpvotes);
        commentTypeBox = findViewById(R.id.commentTypeBox);
        postDetail = findViewById(R.id.postDetail);
        tb = findViewById(R.id.forumPostToolbar);
        postCommentRec = findViewById(R.id.postCommentsSection);
        setSupportActionBar(tb);
        ab = getSupportActionBar();
        tb.setTitleTextColor(getResources().getColor(R.color.colorWhite));
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
        }
        colRef = db.collection("forum_posts").document(id).collection("comments")
                .orderBy("commenttime",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<CommentPostInfo> options = new FirestoreRecyclerOptions.Builder<CommentPostInfo>()
                .setQuery(colRef,CommentPostInfo.class)
                .build();
        adapter = new CommentPostAdapter(options,getApplicationContext(),id)
        {
            @Override
            public void onDataChanged()
            {
                dismissSnackBar();
            }
        };
        postCommentRec.setAdapter(adapter);
        postCommentRec.setHasFixedSize(false);
        postCommentRec.setLayoutManager(new LinearLayoutManager(this));
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!commentTypeBox.getText().toString().equals("")) {
                    uploadAndSaveComment(commentTypeBox.getText().toString(), id);
                    commentTypeBox.setText("");
                }
            }
        });
        adapter.startListening();
    }
    public void uploadAndSaveComment(String commentText,final String id)
    {
        CommentPostInfo postObject = new CommentPostInfo(authUid,commentText);
        db.collection("forum_posts").document(id).collection("comments")
                .add(postObject).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String commentID = Objects.requireNonNull(task.getResult()).getId();
                db.collection("forum_posts").document(id).collection("comments")
                        .document(commentID).update("commentid",commentID);
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

    public void dismissSnackBar() {
            loadingSnack.dismiss();
    }
}