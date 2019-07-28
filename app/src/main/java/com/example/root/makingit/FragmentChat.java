package com.example.root.makingit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentChat extends FragmentDept {
    RecyclerView chatList;
    ChatMainAdapter chatsAdapter;
    private FirebaseAuth auth  = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference eventRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        myListener = (departmentListener) getActivity();
        if(myListener!=null) {
            myListener.setActionBarTitle("Chats");
            myListener.makeLoadingSnackBar("Loading Chats...");
        }
        chatList = view.findViewById(R.id.chatListRecycler);
        assert auth.getUid()!=null;
        eventRef = db.collection("users").document(auth.getUid())
                .collection("chats");
        Query query = eventRef.orderBy("chattime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMainModel> options = new FirestoreRecyclerOptions.Builder<ChatMainModel>()
                .setQuery(query,ChatMainModel.class)
                .build();
        chatsAdapter = new ChatMainAdapter(options,getActivity())
        {
            @Override
            public void onDataChanged()
            {
                myListener.dismissSnackBar();
            }
        };
        chatList.setAdapter(chatsAdapter);
        chatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatList.setHasFixedSize(false);
        chatsAdapter.startListening();
        chatsAdapter.setOnItemClickListener(new ChatMainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                Intent i = new Intent(getActivity(), ChatScreenActivity.class);
                i.putExtra("userId",id);
                startActivity(i);
            }
        });
        return view;
    }
}
