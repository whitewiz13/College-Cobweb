package com.example.root.makingit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    int count=0;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Get the data to send to notification
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("body");
        String author = data.get("author");
        String dept = data.get("dept");
        count++;
        checkCreator(author,title,message,dept);
    }
    public void checkCreator(String author,String title, String message,String dept)
    {
        if(!author.equals(FirebaseAuth.getInstance().getUid()))
        {
            if(dept == null) {
                sendEventNotification(title, message);
            }
            else
            {
                sendDeptNotification(title,message);
            }

        }
    }

    public void sendEventNotification(String title, String message) {
        // Create an explicit intent for an Activity in your app
        int SUMMARY_ID = 0;
        String EVENT_GROUP_KEY = "EVENT_GROUP";
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
            CharSequence name = "CH01";
            String CHANNEL_ID = "CHID";
            String description = "Base Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationManager notificationManager;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_noti)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setGroup(EVENT_GROUP_KEY)
                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            Notification summaryNotification =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("New Events!")
                            //set content text to support devices running API level < 24
                            .setContentText("You've got new events")
                            .setSmallIcon(R.drawable.ic_noti)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            //build summary info into InboxStyle template
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(title + " " + message)
                                    .addLine("....")
                                    .setBigContentTitle(title)
                                    .setSummaryText("You've got new events"))
                            //specify which group this notification belongs to
                            .setGroup(EVENT_GROUP_KEY)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent)
                            .build();
            int num = (int) System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(num, builder.build());
                notificationManager.notify(SUMMARY_ID,summaryNotification);
            }
            else
            {
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(num, builder.build());
                notificationManager.notify(SUMMARY_ID,summaryNotification);
            }
    }
    public void sendDeptNotification(String title, String message) {
        // Create an explicit intent for an Activity in your app
        int SUMMARY_ID = 1;
        String DEPT_EVENT_GROUP_KEY = "DEPT_EVENT_GROUP";
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "CH01";
        String CHANNEL_ID = "CHID";
        String description = "Base Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationManager notificationManager;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle(title)
                .setContentText(message)
                .setGroup(DEPT_EVENT_GROUP_KEY)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification summaryNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("New Events!")
                        //set content text to support devices running API level < 24
                        .setContentText("You've got new events")
                        .setSmallIcon(R.drawable.ic_noti)
                        .setColor(getResources().getColor(R.color.colorPrimaryDark))
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine(title + " " + message)
                                .addLine("....")
                                .setBigContentTitle(title)
                                .setSummaryText("You've got new dept events"))
                        //specify which group this notification belongs to
                        .setGroup(DEPT_EVENT_GROUP_KEY)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .setContentIntent(pendingIntent)
                        .build();
        int num = (int) System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(num, builder.build());
            notificationManager.notify(SUMMARY_ID,summaryNotification);
        }
        else
        {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(num, builder.build());
            notificationManager.notify(SUMMARY_ID,summaryNotification);
        }
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