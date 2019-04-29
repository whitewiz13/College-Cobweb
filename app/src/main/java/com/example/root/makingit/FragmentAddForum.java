package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class FragmentAddForum extends FragmentAddEvent {
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_forum, viewGroup, false);
        final EditText ename,edetail;
        listner = (onActionListener) getActivity();
        Button enter = view.findViewById(R.id.forumEnter);
        ename = view.findViewById(R.id.forumName);
        edetail = view.findViewById(R.id.forumDetail);
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
                saveForumData(evname,evdetail);
                listner.dismissMe(frag);
                listner.makeSnackB("Event (".concat(evname).concat(") Created Successfully!"));

            }
        });
        return view;
    }
    public void saveForumData(String evname,String evdetail)
    {
        String evauthor;
        evauthor =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("forum_posts").document();
        ForumPostInfo forumpost = new ForumPostInfo(docRef.getId(),evname,evdetail,evauthor,"0","0");
        docRef.set(forumpost);
    }
}
