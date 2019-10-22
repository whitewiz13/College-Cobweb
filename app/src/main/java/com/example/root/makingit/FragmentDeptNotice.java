package com.example.root.makingit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class FragmentDeptNotice extends Fragment {
    departmentListener myListener;
    interface departmentListener{
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    List<DeptEventInfo> deptEventList = new ArrayList<>();
    ProgressBar progressBar;
    RecyclerView recyclerView;
    DocumentSnapshot lastVisible=null;
    String dept;
    DeptEventInfoAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    TextView mainHeading;
    NestedScrollView deptScroll;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dept_notice,viewGroup, false);
        progressBar = view.findViewById(R.id.deptEventProgressBar);
        deptScroll = view.findViewById(R.id.deptEventNoticeScrollview);
        myListener = (departmentListener) getActivity();
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
                        mainHeading.setText(documentSnapshot.getString("dept_name"));
                        eventRef = db.collection("institute_list").document(dept).collection("events");
                        loadList();
                    }
                });
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeContainer = view.findViewById(R.id.swipeView);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        return view;
    }
    public void added(DeptEventInfo deptEventInfo)
    {
        deptEventList.add(0,deptEventInfo);
        adapter.notifyDataSetChanged();
    }
    public void loadList()
    {
        deptEventList.clear();
        progressBar.setVisibility(View.VISIBLE);
        Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING).limit(15);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    deptEventList.add(documentSnapshot.toObject(DeptEventInfo.class));
                }
                if(queryDocumentSnapshots.size()!=0) {
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                }
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        adapter = new DeptEventInfoAdapter(deptEventList, getActivity(), new DeptEventInfoAdapter.OnActionListener() {
            @Override
            public void showSnackBar(String msg) {
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
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        if (deptScroll != null) {
            deptScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
            final int lastSize = deptEventList.size();
            final Query query = eventRef.orderBy("edate", Query.Direction.DESCENDING).startAfter(lastVisible)
                    .limit(10);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        deptEventList.add(documentSnapshot.toObject(DeptEventInfo.class));
                    }
                    if(queryDocumentSnapshots.size()!=0) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        adapter.notifyItemRangeInserted(lastSize,deptEventList.size());
                    }
                    else {
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}