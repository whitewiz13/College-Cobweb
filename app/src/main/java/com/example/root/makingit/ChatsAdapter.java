package com.example.root.makingit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ChatsAdapter extends FirestoreRecyclerAdapter<ChatsModel,ChatsAdapter.MyViewHolder> {

    private Context mContext;
    public ChatsAdapter(@NonNull FirestoreRecyclerOptions<ChatsModel> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }
    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ChatsModel model) {
        holder.message.setText(model.getMessage());
        Date date = model.getChattime();
        String creationDate = "Sending..";
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            creationDate = dateFormat.format(date);
        }
        holder.chatTime.setText(creationDate);
        setDifferentDesign(holder,model);
    }
    public void setDifferentDesign(MyViewHolder holder,ChatsModel model)
    {
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        String authId = auth.getUid();
        String compID = model.getChatpersonid();
        if(Objects.equals(compID, authId))
        {
            holder.message.setTextColor(Color.BLACK);
            holder.message.setGravity(Gravity.END);
            holder.chatTime.setGravity(Gravity.END);
            holder.chatReceiverLayout.setGravity(Gravity.END);
            holder.chatInnerLayout.setBackground(mContext.getDrawable(R.drawable.background_border_black));
            holder.chatInnerLayout.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorWhite));

        }
        else
        {
            holder.message.setTextColor(Color.BLACK);
            holder.message.setGravity(Gravity.START);
            holder.chatTime.setGravity(Gravity.START);
            holder.chatReceiverLayout.setGravity(Gravity.START);
            holder.chatInnerLayout.setBackground(mContext.getDrawable(R.drawable.background_border_sender));
            holder.chatInnerLayout.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorCreme));
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_receiver_card, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message, chatTime;
        RelativeLayout chatReceiverLayout,chatInnerLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatMessage);
            chatTime = itemView.findViewById(R.id.chatTime);
            chatReceiverLayout = itemView.findViewById(R.id.chatReceiverLayout);
            chatInnerLayout = itemView.findViewById(R.id.chatReceiverInnerLayout);
        }
    }
}
