package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class FragmentForum extends Fragment {
    interface onDoStuffForActivity {
        void setActionBarTitle(String title);
        void makeSnackB(String msg);
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    private NestedScrollView nestedScrollView;
    ProgressBar forumProgressBar;
    SwipeRefreshLayout forumSwipeRefresh;
    public onDoStuffForActivity doStuffListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection("forum_posts");
    ForumPostAdapter adapter;
    RecyclerView forumRecycler;
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        doStuffListener = (onDoStuffForActivity) getActivity();
        if (doStuffListener != null) {
            doStuffListener.makeLoadingSnackBar("Loading Forums...");
            doStuffListener.setActionBarTitle("Forum");
        }
        View view = inflater.inflate(R.layout.fragment_forum, viewGroup, false);
        nestedScrollView = view.findViewById(R.id.forumNestedScroll);
        forumRecycler = view.findViewById(R.id.forumRecycler);
        forumProgressBar = view.findViewById(R.id.forumProgressBar);
        forumSwipeRefresh = view.findViewById(R.id.forumSwipeRefresh);
        forumSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts();
            }
        });
        loadPosts();
        return view;
    }
    List<ForumPostInfo> forumList =new ArrayList<>();
    DocumentSnapshot lastVisible=null;

    public void loadPosts() {
        forumList.clear();
        forumProgressBar.setVisibility(View.VISIBLE);
        Query query = eventRef.orderBy("fdate", Query.Direction.DESCENDING).limit(5);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    forumList.add(documentSnapshot.toObject(ForumPostInfo.class));
                }
                if (queryDocumentSnapshots.size() != 0) {
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                }
                doStuffListener.dismissSnackBar();
                forumSwipeRefresh.setRefreshing(false);
                adapter.notifyDataSetChanged();
                forumProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        adapter = new ForumPostAdapter(forumList, getActivity(), new ForumPostAdapter.OnActionListener() {
            @Override
            public void showSnackBar(String msg) {
                doStuffListener.makeSnackB(msg);
            }
        });

        forumRecycler.setAdapter(adapter);
        forumRecycler.setItemViewCacheSize(20);
        forumRecycler.setDrawingCacheEnabled(true);
        forumRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        forumRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        forumProgressBar.setVisibility(View.VISIBLE);
                        loadNextForum();
                    }
                }
            });
        }
    }
    public void loadNextForum()
    {
        if(lastVisible!=null) {
            final int lastSize = forumList.size();
            final Query query = eventRef.orderBy("fdate", Query.Direction.DESCENDING).startAfter(lastVisible)
                    .limit(10);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        forumList.add(documentSnapshot.toObject(ForumPostInfo.class));
                    }
                    if(queryDocumentSnapshots.size()!=0) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                    }
                    else
                    {
                        lastVisible=null;
                        forumProgressBar.setVisibility(View.INVISIBLE);
                    }
                    adapter.notifyItemRangeInserted(lastSize,forumList.size());
                    forumProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            forumProgressBar.setVisibility(View.INVISIBLE);
            doStuffListener.makeSnackB("End of Forums!");
        }
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addEventButton).setVisible(false);
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}