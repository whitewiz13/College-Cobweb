package com.example.root.makingit;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
    Boolean searching = false;
    List<EventInfo> eventList = new ArrayList<>();
    List<EventInfo> allList = new ArrayList<>();
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
            doStuffListener.makeLoadingSnackBar("Loading Events...");
            doStuffListener.setActionBarTitle("Events");
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        loadEventList();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!searching)
                    loadEventList();
                else
                    swipeContainer.setRefreshing(false);
            }
        });
        return view;
    }
    public void loadEventList()
    {
        eventList.clear();
        allList.clear();
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
        if(getActivity()!=null)
            adapter = new EventRecyclerAdapter(eventList,getActivity().getApplicationContext(), new EventRecyclerAdapter.OnActionListener() {
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1)
        {
            @Override
            public boolean supportsPredictiveItemAnimations()
            {
                return true;
            }
        };
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
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
        //NOT DISPLAYING
        final Query query2 = eventRef.orderBy("edate", Query.Direction.DESCENDING);
        query2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    allList.add(documentSnapshot.toObject(EventInfo.class));
                }
            }
        });
    }
    public void loadNextList()
    {
        if(lastVisible!=null && !searching) {
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
        }
    }
    public void added(EventInfo eventInfo)
    {
        eventList.add(0,eventInfo);
        adapter.notifyDataSetChanged();
    }
    public void showSearchResult(String searchText)
    {
        searchText = searchText.toLowerCase();
        progressBar.setVisibility(View.VISIBLE);
        final List<EventInfo> resultList = new ArrayList<>();
        resultList.clear();
        for (EventInfo eventInfo : allList)  {
            if (eventInfo.getEname() != null && eventInfo.getEname().toLowerCase().contains(searchText) || eventInfo.getEdetail().toLowerCase().contains(searchText)) {
                resultList.add(eventInfo);
            }
        }
        adapter = new EventRecyclerAdapter(resultList, getContext(), new EventRecyclerAdapter.OnActionListener() {
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1)
        {
            @Override
            public boolean supportsPredictiveItemAnimations()
            {
                return true;
            }
        };
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        MenuItem menuitem = menu.findItem(R.id.searchButton);
        SearchView mSearchView = (SearchView) menuitem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {
                    showSearchResult(newText);
                    searching=true;
                }
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                loadEventList();
                searching = false;
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }
}