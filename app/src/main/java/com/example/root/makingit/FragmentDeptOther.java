package com.example.root.makingit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import javax.annotation.Nullable;

public class FragmentDeptOther extends Fragment{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    PeerInfoRecyclerAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dept_other,viewGroup, false);
        final RecyclerView peerRecycler;
        peerRecycler = view.findViewById(R.id.peer_recyler);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore mydb = FirebaseFirestore.getInstance();
        mydb.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Query query = userRef.whereEqualTo("dept", Objects.requireNonNull(documentSnapshot).getString("dept"));
                        FirestoreRecyclerOptions<UserInfo> options = new FirestoreRecyclerOptions.Builder<UserInfo>()
                                .setQuery(query,UserInfo.class)
                                .build();
                        adapter = new PeerInfoRecyclerAdapter(options,getContext());
                        adapter.startListening();
                        peerRecycler.setAdapter(adapter);
                        peerRecycler.setHasFixedSize(false);
                        peerRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapter.setOnItemClickListener(new PeerInfoRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                                String id = documentSnapshot.getId();
                                Intent i = new Intent(getActivity(), UserProfileOpenActivity.class);
                                i.putExtra("userId",id);
                                startActivity(i);
                            }
                        });
                    }
                });
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(adapter!=null)
        {
            adapter.stopListening();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //adapter.startListening();
    }
}