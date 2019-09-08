package com.example.root.makingit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMainAdapter extends FirestoreRecyclerAdapter<ChatMainModel,ChatMainAdapter.MyViewHolder> {


    private Context mContext;
    private OnItemClickListener listener;
    public ChatMainAdapter(@NonNull FirestoreRecyclerOptions<ChatMainModel> options,Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position,final @NonNull ChatMainModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.lastmessage.setText(model.getMessage());
        holder.uname.setText(model.getUname());
        holder.chatuserid.setText(model.getUsernameid());
        setUpDateAndTime(model.getChattime(),holder);
        db.collection("users").document(model.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {

                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null) {
                                GlideApp.with(mContext)
                                        .load(documentSnapshot.getString("uimage"))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.loadme)
                                        .into(holder.chatImage);
                            }
                        }catch (Exception e)
                        {
                            Toast.makeText(mContext,"Turn on network connection!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void setUpDateAndTime(Date date,MyViewHolder holder)
    {
        Date nowDate = new Date();
        String creationDate = "just now";
        long timeInMilliSeconds = 0;
        if(date!=null)
            timeInMilliSeconds = nowDate.getTime()-date.getTime();
        long seconds = timeInMilliSeconds / 1000;
        if(seconds < 60)
            holder.chattime.setText("Just now");
        else if(nowDate.getDay() > date.getDay())
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM hh:mm a", Locale.ENGLISH);
            creationDate = dateFormat.format(date);
            holder.chattime.setText(creationDate);
        }
        else
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            creationDate = dateFormat.format(date);
            holder.chattime.setText(creationDate);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_main_screen, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView uname,lastmessage,chattime,chatuserid;
        CircleImageView chatImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            chatuserid = itemView.findViewById(R.id.chatUserId);
            uname = itemView.findViewById(R.id.chatUserName);
            lastmessage = itemView.findViewById(R.id.chatLastMessage);
            chattime = itemView.findViewById(R.id.chatMainTime);
            chatImage = itemView.findViewById(R.id.chatProfilePic);
            itemView.setOnClickListener(new View.OnClickListener() {
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