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
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentBrowseInstitute extends Fragment {
    ProgressBar insProgress;
    RecyclerView browseInList;
    onDoStuffForActivity doStuffListener;
    InstituteListAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference instRef = db.collection("institute_list");
    List<CollegeInfo> collegeInfoList = new ArrayList<>();
    DocumentSnapshot lastVisible=null;
    interface onDoStuffForActivity{
        void setActionBarTitle(String title);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_browse_institutes,container,false);
        insProgress  =view.findViewById(R.id.instituteProgressBar);
        browseInList = view.findViewById(R.id.browseInList);
        doStuffListener = (onDoStuffForActivity) getActivity();
        if(doStuffListener!=null)
            doStuffListener.setActionBarTitle("Browse Institute");
        loadInstituteList("collegeRating",Query.Direction.DESCENDING);
        return view;
    }
    public void loadInstituteList(String sort,Query.Direction direction)
    {
        Query query = instRef.orderBy(sort, direction);
        collegeInfoList.clear();
        insProgress.setVisibility(View.VISIBLE);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collegeInfoList.add(documentSnapshot.toObject(CollegeInfo.class));
                }
                if(queryDocumentSnapshots.size()!=0) {
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                }
                adapter.notifyDataSetChanged();
                insProgress.setVisibility(View.INVISIBLE);
            }
        });
        adapter = new InstituteListAdapter(collegeInfoList,getContext());
        browseInList.setAdapter(adapter);
        browseInList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(menu.findItem(R.id.sortElements)!=null)
            menu.findItem(R.id.sortElements).setVisible(false);
        //if(menu.findItem(R.id.addEventButton)!=null) {
        //    menu.findItem(R.id.addEventButton).setVisible(false);
        //}
        if(menu.findItem(R.id.addForumPostButton)!=null) {
            menu.findItem(R.id.addForumPostButton).setVisible(false);
        }
        if(menu.findItem(R.id.addDeptEventButton)!=null)
        {
            menu.findItem(R.id.addDeptEventButton).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }
}
