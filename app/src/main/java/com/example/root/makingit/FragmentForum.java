package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class FragmentForum extends Fragment {
    interface onDoStuffForActivity {
        void setActionBarTitle(String title);
    }
    LinearLayoutManager mLayoutManager;
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
            doStuffListener.setActionBarTitle("Forum");
        }
        View view = inflater.inflate(R.layout.fragment_forum, viewGroup, false);
        forumRecycler = view.findViewById(R.id.forumRecycler);
        loadPosts();
        return view;
    }
    public void loadPosts() {
        Query query = eventRef.orderBy("fdate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ForumPostInfo> options = new FirestoreRecyclerOptions.Builder<ForumPostInfo>()
                .setQuery(query, ForumPostInfo.class).build();
        adapter = new ForumPostAdapter(options, getActivity());
        forumRecycler.setAdapter(adapter);
        forumRecycler.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        forumRecycler.setLayoutManager(mLayoutManager);
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addEventButton).setVisible(false);
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}