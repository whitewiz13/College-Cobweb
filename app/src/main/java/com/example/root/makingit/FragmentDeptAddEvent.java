package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FragmentDeptAddEvent extends DialogFragment {
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    DialogFragment frag = this;
    private onActionListener listner;
    interface onActionListener
    {
        void dismissMe(DialogFragment frag);
        void makeSnackB(String msg);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dept_add_event,viewGroup, false);
        final EditText ename,edetail;
        listner = (onActionListener) getActivity();
        Button enter = view.findViewById(R.id.denter);
        ename = view.findViewById(R.id.devname);
        edetail = view.findViewById(R.id.devdetail);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String evname,evdetail;
                evname = ename.getText().toString();
                evdetail = edetail.getText().toString();
                if (TextUtils.isEmpty(evname)) {
                    listner.makeSnackB("Enter Dept Event Name!");
                    return;
                }

                if (TextUtils.isEmpty(evdetail)) {
                    listner.makeSnackB("Enter Dept Event Detail!");
                    return;
                }
                saveEventData(evname,evdetail);
                listner.dismissMe(frag);
                listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));
            }
        });
        return view;
    }
    public void saveEventData(final String evname,final String evdetail)
    {
        final String evauthor;
        evauthor =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final  FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference mydb = db.collection("users").document(evauthor);
        mydb.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentReference docRef = db.collection(Objects.requireNonNull(documentSnapshot.getString("dept"))).document("events").collection("events").document();
                DeptEventInfo deptEvent = new DeptEventInfo(docRef.getId(),evname,evdetail,evauthor,documentSnapshot.getString("dept"));
                docRef.set(deptEvent);
            }
        });
    }
}