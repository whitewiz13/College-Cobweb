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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.net.CookieHandler;
import java.util.Objects;

import javax.annotation.Nullable;

public class FragmentDeptSubject extends Fragment {
    RecyclerView subList;
    String dept;
    String sem;
    SubjectInfoAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference subjectRef;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dept_subject, viewGroup, false);

        subList = view.findViewById(R.id.subjectList);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore mydb = FirebaseFirestore.getInstance();
        mydb.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        assert documentSnapshot != null;
                        dept = documentSnapshot.getString("dept");
                        subjectRef = db.collection(dept).document("subjects").collection("sem1");
                        okayDoNow();
                    }
                });
        return view;
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
