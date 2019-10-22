package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class FragmentAlumni extends Fragment {
    RecyclerView alumniRecycler;
    Button registerAlumni;
    int hideIt =0;
    AlumniAdapter adapter;
    List<AlumniModel> alumniList = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        try {
            if (getArguments() != null) {
                hideIt= getArguments().getInt("hideIt");
            }
        }
        catch (Exception e)
        {
            //Later
        }
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_dept_alumni, viewGroup, false);
        registerAlumni = view.findViewById(R.id.registerAlumniButton);
        if(hideIt==1)
        {
            registerAlumni.setVisibility(View.GONE);
        }
        alumniRecycler = view.findViewById(R.id.alumniRecycler);
        alumniList.add(new AlumniModel("someone_popular (Batch of 2006)",
                "'You can do anything with money!","CEO of Microsoft'","2011-2013",
                "Founder of Uber","2014-Present"));
        alumniList.add(new AlumniModel("cool_bean (Batch of 2008)",
                "'I am very rich now!'","CEO of Google","2012-2015",
                "Founder of Apple","2016-Present"));
        adapter = new AlumniAdapter(alumniList,getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1)
        {
            @Override
            public boolean supportsPredictiveItemAnimations()
            {
                return true;
            }
        };
        alumniRecycler.setLayoutManager(gridLayoutManager);
        adapter.setHasStableIds(true);
        alumniRecycler.setAdapter(adapter);
        return view;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {
            menu.findItem(R.id.addDeptEventButton).setVisible(false);
        }catch (Exception e)
        {
            //DO nothing
        }
        super.onPrepareOptionsMenu(menu);
    }
}
