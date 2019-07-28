package com.example.root.makingit;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    private onDoStuffForActivity doStuffListener;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_event, viewGroup, false);
        eventScroll = view.findViewById(R.id.EVntscrollview);
        swipeContainer = view.findViewById(R.id.swipeView);
        doStuffListener = (onDoStuffForActivity) getActivity();
        if (doStuffListener != null) {
            doStuffListener.makeLoadingSnackBar("Loading Events..");
            doStuffListener.setActionBarTitle("Events");
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        loadEventList();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEventList();
            }
        });
        return view;
    }

    public void loadEventList()
    {
        final Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<EventInfo> options = new FirestoreRecyclerOptions.Builder<EventInfo>()
                .setQuery(query,EventInfo.class)
                .build();
        adapter = new EventRecyclerAdapter(options,getActivity().getApplicationContext(), new EventRecyclerAdapter.OnActionListener() {
            @Override
            public void showSnackBar(String msg) {
                doStuffListener.makeSnackB(msg);
            }
            @Override
            public void makeLoadingSnackBar(String msg) {
                doStuffListener.makeLoadingSnackBar(msg);
            }

            @Override
            public void dismissSnackBar() {
                doStuffListener.dismissSnackBar();

            }
        })
        {
            @Override
            public void onDataChanged()
            {
                doStuffListener.dismissSnackBar();
                swipeContainer.setRefreshing(false);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.startListening();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.stopListening();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //adapter.startListening();
    }
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}