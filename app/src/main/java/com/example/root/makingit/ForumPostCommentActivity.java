package com.example.root.makingit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

public class ForumPostCommentActivity extends AppCompatActivity {
    String id;
    TextView postName,postDetail;
    Toolbar tb;
    ActionBar ab;
    public FirebaseAuth auth;
    public FirebaseFirestore db= FirebaseFirestore.getInstance();
    ForumPostInfo model;
    RecyclerView postCommentRec;
    CommentPostAdapter adapter;
    Query colRef;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_post_comment_activity);
        postName = findViewById(R.id.postName);
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
                            postName.setText(model.getFname());
                            postDetail.setText(model.getFdetail());
                            ab.setTitle(model.getFname());
                        }
                    }
                }
            });
        }
        colRef = db.collection("forum_posts").document(id).collection("comments");
        FirestoreRecyclerOptions<CommentPostInfo> options = new FirestoreRecyclerOptions.Builder<CommentPostInfo>()
                .setQuery(colRef,CommentPostInfo.class).setLifecycleOwner(this)
                .build();
        adapter = new CommentPostAdapter(options);
        postCommentRec.setAdapter(adapter);
        postCommentRec.setHasFixedSize(false);
        postCommentRec.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}