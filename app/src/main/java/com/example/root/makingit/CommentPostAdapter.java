package com.example.root.makingit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class CommentPostAdapter extends FirestoreRecyclerAdapter<CommentPostInfo, CommentPostAdapter.MyViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colref =db.collection("users");
    public CommentPostAdapter(@NonNull FirestoreRecyclerOptions<CommentPostInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull CommentPostInfo model) {
        holder.commentDetail.setText(model.getcdetail());
        getUserInfo(model.getcauthor(),holder);
    }
    public void getUserInfo(String authorId,final MyViewHolder holder)
    {
        colref.document(authorId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot!=null)
                    holder.commentAuthor.setText(documentSnapshot.getString("name"));
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_post_card, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView commentDetail,commentAuthor;
        public MyViewHolder(View view)
        {
            super(view);
            commentDetail = view.findViewById(R.id.commentDetail);
            commentAuthor = view.findViewById(R.id.commentAuthor);
        }
    }
}
