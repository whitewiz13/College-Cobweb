package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentBrowseInstitute extends Fragment {
    RecyclerView browseInList;
    onDoStuffForActivity doStuffListener;
    InstituteListAdapter adapter;
    List<CollegeInfo> collegeInfoList = new ArrayList<>();
    interface onDoStuffForActivity{
        void setActionBarTitle(String title);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_institutes,container,false);
        doStuffListener = (onDoStuffForActivity) getActivity();
        if(doStuffListener!=null)
            doStuffListener.setActionBarTitle("Browse Institute");
        collegeInfoList.add(new CollegeInfo("GOVT COLLEGE KULLU","KULLU","5","Nice Place"));
        collegeInfoList.add(new CollegeInfo("GOVT COLLEGE MANDI","KULLU","5","Okay Place"));
        adapter = new InstituteListAdapter(collegeInfoList,getContext());
        browseInList = view.findViewById(R.id.browseInList);
        browseInList.setAdapter(adapter);
        browseInList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
        return view;
    }
}
