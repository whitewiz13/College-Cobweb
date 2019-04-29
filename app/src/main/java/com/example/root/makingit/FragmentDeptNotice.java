package com.example.root.makingit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class FragmentDeptNotice extends Fragment {
    RecyclerView recyclerView;
    String dept;
    DeptEventInfoAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    TextView mainHeading;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dept_notice,viewGroup, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore mydb = FirebaseFirestore.getInstance();
        mainHeading = view.findViewById(R.id.theMainHeading);
        mydb.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        assert documentSnapshot != null;
                        dept = documentSnapshot.getString("dept");
                        mainHeading.setText(dept + " Events");
                        eventRef = db.collection(dept).document("events").collection("events");
                        okayDoNow();
                    }
                });
        Button showMore = view.findViewById(R.id.showALl);
        showMore.setTextColor(Color.GRAY);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeContainer = view.findViewById(R.id.swipeView);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.refreshDrawableState();
                swipeContainer.setRefreshing(false);
            }
        });
        return view;
    }
    public void okayDoNow()
    {
        Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<DeptEventInfo> options = new FirestoreRecyclerOptions.Builder<DeptEventInfo>()
                .setQuery(query,DeptEventInfo.class).setLifecycleOwner(this)
                .build();
        adapter = new DeptEventInfoAdapter(options, getActivity(), new DeptEventInfoAdapter.OnActionListener() {
            @Override
            public void showSnackBar(String msg) {
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}