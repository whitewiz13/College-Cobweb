package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeerInfoRecyclerAdapter extends FirestoreRecyclerAdapter<UserInfo,PeerInfoRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private OnItemClickListener listener;
    public PeerInfoRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserInfo> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserInfo model) {
            holder.authorName.setText(model.getName());
            holder.authorRno.setText(model.getRno());
            holder.authorDept.setText(model.getDept());
            GlideApp.with(mContext)
                    .load(model.getUimage())
                    .placeholder(R.drawable.loadme)
                    .into(holder.profileImage);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.peer_list_item_card, parent, false);
        return new MyViewHolder(itemView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView authorName,authorRno,authorDept;
        private CircleImageView profileImage;
        private MyViewHolder(View view)
        {
            super(view);
            authorName = view.findViewById(R.id.peerAuthorText);
            authorRno = view.findViewById(R.id.peerAuthorRno);
            authorDept= view.findViewById(R.id.peerDept);
            profileImage= view.findViewById(R.id.peerProfileImage);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
