package com.example.root.makingit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ForumPostCommentActivity extends AppCompatActivity {
    TextView test;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_post_comment_activity);
        test = findViewById(R.id.postID);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("postId");
            test.setText(id);
        }
    }
}