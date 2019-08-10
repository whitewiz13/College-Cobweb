package com.example.root.makingit;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FragmentEvent extends Fragment{
    List<EventInfo> eventList = new ArrayList<>();
    DocumentSnapshot lastVisible=null;
    private ProgressBar progressBar;
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
        progressBar = view.findViewById(R.id.eventProgressBar);
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
        eventList.clear();
        progressBar.setVisibility(View.VISIBLE);
        final Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING).limit(5);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    eventList.add(documentSnapshot.toObject(EventInfo.class));
                }
                if(queryDocumentSnapshots.size()!=0) {
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                }
                adapter.notifyDataSetChanged();
                doStuffListener.dismissSnackBar();
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        adapter = new EventRecyclerAdapter(eventList,getActivity(), new EventRecyclerAdapter.OnActionListener() {
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
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (eventScroll != null) {
            eventScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        progressBar.setVisibility(View.VISIBLE);
                        loadNextList();
                    }
                }
            });
        }
    }
    public void loadNextList()
    {
        if(lastVisible!=null) {
            final int lastSize = eventList.size();
            final Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING).startAfter(lastVisible)
                    .limit(10);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            eventList.add(documentSnapshot.toObject(EventInfo.class));
                    }
                    if(queryDocumentSnapshots.size()!=0) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        adapter.notifyItemRangeInserted(lastSize,eventList.size());
                    }
                    else
                    {
                        lastVisible=null;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            doStuffListener.makeSnackB("End of Events!");
        }
    }
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        menu.findItem(R.id.searchButton).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                eventList.add(0,new EventInfo("123","123","123","123",null));
                adapter.notifyItemInserted(0);
                adapter.notifyItemRangeChanged(0,eventList.size());
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }
}