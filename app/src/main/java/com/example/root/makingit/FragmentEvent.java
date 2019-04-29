package com.example.root.makingit;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentEvent extends Fragment{
    SwipeRefreshLayout swipeContainer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("events");
    EventRecyclerAdapter adapter;
    NestedScrollView eventScroll;
    RecyclerView recyclerView;
    interface onDoStuffForActivity{
        void setActionBarTitle(String title);
        void makeSnackB(String msg);
    }
    private onDoStuffForActivity doStuffListener;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_event, viewGroup, false);
        eventScroll = view.findViewById(R.id.EVntscrollview);
        Button showMore = view.findViewById(R.id.showALl);
        swipeContainer = view.findViewById(R.id.swipeView);
        showMore.setTextColor(Color.GRAY);
        doStuffListener = (onDoStuffForActivity) getActivity();
        if (doStuffListener != null) {
            doStuffListener.setActionBarTitle("Events");
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<EventInfo> options = new FirestoreRecyclerOptions.Builder<EventInfo>()
                .setQuery(query,EventInfo.class).build();
        adapter = new EventRecyclerAdapter(options, getActivity(), new EventRecyclerAdapter.OnActionListener() {
            @Override
            public void showSnackBar(String msg) {
                doStuffListener.makeSnackB(msg);
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.refreshDrawableState();
                swipeContainer.setRefreshing(false);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    @Override
    public void onStart() {
        adapter.startListening();
        super.onStart();
    }
    @Override
    public void onStop() {
        adapter.stopListening();
        super.onStop();
    }
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}