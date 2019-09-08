package com.example.root.makingit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

public class FragmentDeptOther extends Fragment{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    RecyclerView peerRecycler;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore mydb = FirebaseFirestore.getInstance();
    PeerInfoRecyclerAdapter adapter;
    List<UserInfo> peerList = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_dept_other,viewGroup, false);
        peerRecycler = view.findViewById(R.id.peer_recyler);
        loadPeerList();
        return view;
    }
    public void loadPeerList()
    {
        peerList.clear();
        mydb.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Query query = userRef.whereEqualTo("dept", Objects.requireNonNull(documentSnapshot).getString("dept"));
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            peerList.add(documentSnapshot.toObject(UserInfo.class));
                            adapter = new PeerInfoRecyclerAdapter(peerList,getContext());
                            peerRecycler.setAdapter(adapter);
                            peerRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter.setOnItemClickListener(new PeerInfoRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    UserInfo model = peerList.get(position);
                                    String id = model.getUid();
                                    Intent i = new Intent(getActivity(), UserProfileOpenActivity.class);
                                    i.putExtra("userId",id);
                                    startActivity(i);
                                }
                            });
                        }
                    }
                });

            }
        });
    }
    public void showSearchedPeer(String searchText)
    {
        final List<UserInfo> resultList = new ArrayList<>();
        searchText = searchText.toLowerCase();
        resultList.clear();
            for (UserInfo uInfo : peerList) {
                if (uInfo.getName() != null && uInfo.getName().toLowerCase().contains(searchText) || uInfo.getRno().toLowerCase().contains(searchText)) {
                    resultList.add(uInfo);
                }
            }
            adapter = new PeerInfoRecyclerAdapter(resultList, getContext());
            adapter.setOnItemClickListener(new PeerInfoRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    UserInfo model = resultList.get(position);
                    String id = model.getUid();
                    Intent i = new Intent(getActivity(), UserProfileOpenActivity.class);
                    i.putExtra("userId", id);
                    startActivity(i);
                }
            });
            peerRecycler.setAdapter(adapter);
            peerRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addDeptEventButton).setVisible(false);
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
                    showSearchedPeer(newText);
                }
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                loadPeerList();
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }
}