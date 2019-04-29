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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class FragmentAddEvent extends DialogFragment {
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    DialogFragment frag = this;
    public onActionListener listner;
    interface onActionListener
    {
        void dismissMe(DialogFragment frag);
        void makeSnackB(String msg);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_add_event,viewGroup, false);
        final EditText ename,edetail;
        listner = (onActionListener) getActivity();
        Button enter = view.findViewById(R.id.enter);
        ename = view.findViewById(R.id.evname);
        edetail = view.findViewById(R.id.evdetail);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String evname,evdetail;
                evname = ename.getText().toString();
                evdetail = edetail.getText().toString();
                if (TextUtils.isEmpty(evname)) {
                    listner.makeSnackB("Enter Event Name!");
                    return;
                }

                if (TextUtils.isEmpty(evdetail)) {
                    listner.makeSnackB("Enter Event Detail!");
                    return;
                }
                saveEventData(evname,evdetail);
                listner.dismissMe(frag);
                listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));

            }
        });
        return view;
    }
    public void saveEventData(String evname,String evdetail)
    {
        String evauthor;
        evauthor =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final  FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("events").document();
        EventInfo event = new EventInfo(docRef.getId(),evname,evdetail,evauthor);
        docRef.set(event);
    }
}