package com.example.root.makingit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import javax.annotation.Nullable;

public class FragmentDeptSubject extends Fragment {
    RecyclerView subList;
    String dept;
    String sem="sem1";
    SubjectInfoAdapter adapter;
    TextView sem1,sem2,sem3,sem4;
    FirebaseAuth auth;
    FirebaseFirestore mydb;
    private CollectionReference subjectRef;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dept_subject, viewGroup, false);
        subList = view.findViewById(R.id.subjectList);
        auth = FirebaseAuth.getInstance();
        mydb = FirebaseFirestore.getInstance();
        setListners(view);
        loadsubjects();
        return view;
    }
    public void loadsubjects()
    {
        mydb.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot!=null)
                            dept = documentSnapshot.getString("dept");
                        if(dept!=null)
                            subjectRef = mydb.collection(dept).document("subjects").collection(sem);
                        okayDoNow();
                    }
                });
    }
    public void setListners(View view)
    {
        sem1 = view.findViewById(R.id.sem1);
        sem2 = view.findViewById(R.id.sem2);
        sem3 = view.findViewById(R.id.sem3);
        sem4 = view.findViewById(R.id.sem4);
        sem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sem = "sem1";
                sem1.setTextColor(getResources().getColor(R.color.colorPrimary));
                sem2.setTextColor(getResources().getColor(R.color.colorBlack));
                sem3.setTextColor(getResources().getColor(R.color.colorBlack));
                sem4.setTextColor(getResources().getColor(R.color.colorBlack));
                loadsubjects();
            }
        });
        sem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sem = "sem2";
                sem1.setTextColor(getResources().getColor(R.color.colorBlack));
                sem2.setTextColor(getResources().getColor(R.color.colorPrimary));
                sem3.setTextColor(getResources().getColor(R.color.colorBlack));
                sem4.setTextColor(getResources().getColor(R.color.colorBlack));
                loadsubjects();
            }
        });
        sem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sem = "sem3";
                sem1.setTextColor(getResources().getColor(R.color.colorBlack));
                sem2.setTextColor(getResources().getColor(R.color.colorBlack));
                sem3.setTextColor(getResources().getColor(R.color.colorPrimary));
                sem4.setTextColor(getResources().getColor(R.color.colorBlack));
                loadsubjects();
            }
        });
        sem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sem = "sem4";
                sem1.setTextColor(getResources().getColor(R.color.colorBlack));
                sem2.setTextColor(getResources().getColor(R.color.colorBlack));
                sem3.setTextColor(getResources().getColor(R.color.colorBlack));
                sem4.setTextColor(getResources().getColor(R.color.colorPrimary));
                loadsubjects();
            }
        });
    }
    public void okayDoNow()
    {
        Query query = subjectRef.orderBy("subcode", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<SubjectInfoModel> options = new FirestoreRecyclerOptions.Builder<SubjectInfoModel>()
                .setQuery(query,SubjectInfoModel.class).setLifecycleOwner(this)
                .build();
        adapter = new SubjectInfoAdapter(options);
        subList.setAdapter(adapter);
        subList.setHasFixedSize(false);
        subList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
