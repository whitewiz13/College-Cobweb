package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeptEventInfoAdapter extends FirestoreRecyclerAdapter<DeptEventInfo, DeptEventInfoAdapter.MyViewHolder> {
    private OnActionListener mListener;
    private Context mContext;

    public DeptEventInfoAdapter(@NonNull FirestoreRecyclerOptions<DeptEventInfo> options, Context mContext, DeptEventInfoAdapter.OnActionListener mListener) {
        super(options);
        this.mListener = mListener;
        this.mContext = mContext;
    }

    interface OnActionListener{
        void showSnackBar(String msg);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageView;
        public TextView ename,edetail,edate,showmore,delete,eauthor,eRollno;
        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.peerProfileImage);
            ename = view.findViewById(R.id.ename);
            edetail = view.findViewById(R.id.edetail);
            edate= view.findViewById(R.id.edate);
            showmore= view.findViewById(R.id.showmore);
            delete = view.findViewById(R.id.delete);
            eauthor = view.findViewById(R.id.peerAuthorText);
            eRollno = view.findViewById(R.id.peerAuthorRno);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull DeptEventInfo model) {
        Date date = model.getEdate();
        doDeleteButton(holder,model);
        String creationDate = "Loading..";
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.ENGLISH);
            creationDate = dateFormat.format(date);
        }
        holder.ename.setText(model.getEname());
        holder.edetail.setText(model.getEdetail());
        holder.edate.setText(creationDate);
        getUserInfo(holder, model.getEauthor());
    }
    public void getUserInfo(final MyViewHolder holder, String eauthor)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(eauthor);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    holder.eauthor.setText(snapshot.getString("name"));
                    holder.eRollno.setText(snapshot.getString("rno"));
                    GlideApp.with(mContext)
                            .load(snapshot.getString("uimage"))
                            .placeholder(R.drawable.loadme)
                            .into(holder.imageView);
                }
            }
        });
    }
    public void doDeleteButton(final MyViewHolder holder, final DeptEventInfo album)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showSnackBar("Successfully Deleted!");
                DocumentReference mydb = db.collection("users").document(album.getEauthor());
                mydb.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        db.collection(Objects.requireNonNull(documentSnapshot.getString("dept"))).document("events").collection("events").document(album.getEid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                });
            }
        });
    }
}