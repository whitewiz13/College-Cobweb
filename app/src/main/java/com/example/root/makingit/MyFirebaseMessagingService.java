package com.example.root.makingit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";
    UserInfo myUinfo;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Get the data to send to notification
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("body");
        String author = data.get("author");
        sendEventNotification(title,message);

    }
    public void sendEventNotification(String title,String message)
    {
        //Create Channel for Android Oreo and above
        String GROUP_KEY_GLOBAL_EVENTS = "com.android.example.WORK_EMAIL";
        NotificationCompat.Builder notification;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "CH01";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(true);
        }
        notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_noti)
                .setGroupSummary(true)
                .setGroup(GROUP_KEY_GLOBAL_EVENTS)
                .setChannelId(CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setColor(getResources().getColor(R.color.colorPrimaryDark));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        int num = (int) System.currentTimeMillis();
        notificationManager.notify("App Name",num ,notification.build());
    }
    /*public void getUserInfo(String eauthor, final String title, final String message)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(eauthor);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                myUinfo = snapshot.toObject(UserInfo.class);
                Glide.with(getApplication())
                        .asBitmap()
                        .load(myUinfo.getUimage())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                //sendNotification(title,message,resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        });
    }
    */
}