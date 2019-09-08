package com.example.root.makingit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentChangeUserName extends FragmentAddEvent {
    private Button changeName;
    private TextView newName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_username, viewGroup, false);
        newName = view.findViewById(R.id.newUserName);
        listner = (onActionListener) getActivity();
        changeName = view.findViewById(R.id.changeNameButton);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = newName.getText().toString();
                checkRNumTakenAndSave(name);
            }
        });
        return view;
    }
    public void checkRNumTakenAndSave(final String rnoo)
    {
        changeName.setEnabled(false);
        db.collection("taken_rno").document(rnoo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists())
                    {
                        if(getActivity() !=null)
                            Toast.makeText(getActivity().getApplicationContext(), "Username Already Exists!", Toast.LENGTH_SHORT).show();
                        changeName.setEnabled(true);
                    }
                    else
                    {
                        Map<String,Object> myMap = new HashMap<>();
                        if(auth.getCurrentUser()!=null)
                            myMap.put("more_stuff",auth.getCurrentUser().getUid());
                        db.collection("taken_rno").document(rnoo).set(myMap);
                        db.collection("users").document(auth.getCurrentUser().getUid())
                                .update("rno",rnoo);
                        listner.dismissMe(FragmentChangeUserName.this);
                        listner.makeSnackB("Username changed successfully!");
                    }
                }
            }
        });
    }
}
