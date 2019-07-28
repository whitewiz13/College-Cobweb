package com.example.root.makingit;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDept extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    departmentListener myListener;

    interface departmentListener{
        void setActionBarTitle(String title);
        void makeLoadingSnackBar(String msg);
        void dismissSnackBar();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_dept,viewGroup, false);
        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        myListener = (departmentListener) getActivity();
        if (myListener != null) {
            myListener.setActionBarTitle("My Department");
        }
        return view;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentDeptNotice(), "Notice");
        adapter.addFragment(new FragmentDeptSubject(), "Subject Help");
        adapter.addFragment(new FragmentDeptOther(), "Peers");
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addEventButton).setVisible(false);
        menu.findItem(R.id.addForumPostButton).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}