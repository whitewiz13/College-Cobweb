package com.example.root.makingit;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class ChatScreenActivity extends AppCompatActivity {

    String authUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    String uname;
    Snackbar loadingSnack;
    Toolbar tb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView messageList;
    ChatsAdapter adapter;
    EditText realMessage;
    Button sendMessage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String id = Objects.requireNonNull(extras).getString("userId");
        setContentView(R.layout.activity_chat_screen);
        makeLoadingSnackBar("Loading...");
        tb = findViewById(R.id.chatScreenToolbar);
        setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        ActionBar actionbar = getSupportActionBar();
        setupToolbar(actionbar,id);
        messageList = findViewById(R.id.chatMessageList);
        realMessage = findViewById(R.id.chatTypeBox);
        sendMessage = findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!realMessage.getText().toString().equals("")) {
                    uploadAndSendMessage(realMessage.getText().toString(), id);
                    realMessage.setText("");
                }
            }
        });
        loadChat(id);
    }
    public void setupToolbar(final ActionBar actionBar,String id)
    {
        db.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null)
                actionBar.setTitle(documentSnapshot.getString("name"));
            }
        });
        db.collection("users").document(Objects.requireNonNull(authUid)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null)
                uname = documentSnapshot.getString("name");
            }
        });
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }
    public void uploadAndSendMessage(String message,String id)
    {
        ChatsModel chatsModel = new ChatsModel(message,authUid,id,"false",uname);
        ChatsModel chatsModel2 = new ChatsModel(message,authUid,id,"true",uname);
        db.collection("users").document(id).collection("chats")
                .document(Objects.requireNonNull(authUid)).collection("messages").add(chatsModel);

        db.collection("users").document(authUid).collection("chats")
                .document(id).collection("messages").add(chatsModel2);
        saveToMainChat(message,chatsModel,db,authUid,id);
    }
    public void saveToMainChat(final String messsage,final ChatsModel chatsModel,final FirebaseFirestore db, final String authUid,final String id)
    {
        db.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    db.collection("users").document(Objects.requireNonNull(authUid)).collection("chats")
                            .document(id).set(new ChatMainModel(messsage,chatsModel.getChattime(),id,documentSnapshot.getString("name"),
                            documentSnapshot.getString("rno")));
                }
            }
        });

        db.collection("users").document(Objects.requireNonNull(authUid)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    db.collection("users").document(id).collection("chats")
                            .document(authUid).set(new ChatMainModel(messsage,chatsModel.getChattime(),authUid,documentSnapshot.getString("name"),
                            documentSnapshot.getString(("rno"))));
                }
            }
        });
    }
    public void loadChat(String id)
    {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        CollectionReference collectionReference = db.collection("users").document(authUid).collection("chats")
                .document(id).collection("messages");
        Query query = collectionReference.orderBy("chattime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ChatsModel> options = new FirestoreRecyclerOptions.Builder<ChatsModel>()
                .setQuery(query,ChatsModel.class)
                .build();
        adapter = new ChatsAdapter(options)
        {
          @Override
          public void onDataChanged(){
              dismissSnackBar();
          }
        };
        messageList.setAdapter(adapter);
        messageList.setItemViewCacheSize(20);
        messageList.setDrawingCacheEnabled(true);
        messageList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        messageList.setLayoutManager(linearLayoutManager);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                messageList.scrollToPosition(adapter.getItemCount()-1);
            }
        });
        messageList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                messageList.scrollToPosition(adapter.getItemCount()-1);
            }
        });
    }
    public void onBackPressed()
    {
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void makeLoadingSnackBar(String msg) {
            loadingSnack = Snackbar.make(findViewById(R.id.chatScreenLayout), msg, Snackbar.LENGTH_INDEFINITE);
            loadingSnack.show();
    }

    public void dismissSnackBar() {
            loadingSnack.dismiss();
    }
}
