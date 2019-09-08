package com.example.root.makingit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    static int count=0,deptcount=0,chatcount=0;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Get the data to send to notification
        Map<String, String> data = remoteMessage.getData();
        String sender = data.get("sender");
        String message = data.get("body");
        String upvoterId = data.get("");
        if(sender != null) {
            String authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            String receiver =data.get("receiver");
            String checkSender = data.get("checksender");
            String senderName = data.get("sendername");
            sendChatNotification(authId,message,receiver,checkSender,senderName,sender);
        }
        else {
            String title = data.get("title");
            String author = data.get("author");
            String dept = data.get("dept");
            checkCreator(author, title, message, dept);
        }
    }
    public void sendChatNotification(String authId, String message,String receiverId,String checkSender,String senderName,String senderId)
    {
        if(receiverId.equals(authId) && checkSender.equals("false"))
        {
            chatcount++;
            int SUMMARY_ID = 2;
            int num = (int) System.currentTimeMillis();
            String CHAT_GROUP_KEY = "CHAT_GROUP";
            //Creating intents to launch activities
            Intent i = new Intent(this, ChatScreenActivity.class);
            i.putExtra("userId",senderId);
            PendingIntent sendToChat = PendingIntent.getActivity(this,num,i,0);
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("fragmentChat", "1");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.cuhplogo);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, num, intent, 0);
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "CH02";
            String CHANNEL_ID = "CHANNELCHATNOTIFICATION";
            String description = "CHAT Channel";
            NotificationManager notificationManager;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.cuhplogo)
                    .setContentTitle(senderName)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setGroup(CHAT_GROUP_KEY)
                    .setLargeIcon(bmp)
                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(sendToChat)
                    .setAutoCancel(true);
            Notification summaryNotification =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("Student Helper")
                            //set content text to support devices running API level < 24
                            .setContentText(chatcount + " new messages")
                            .setSmallIcon(R.drawable.cuhplogo)
                            .setDeleteIntent(getDeleteIntent())
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            //build summary info into InboxStyle template
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(senderName + " : " + message)
                                    .addLine("Tap to check!")
                                    .setBigContentTitle("College Cobweb")
                                    .setSummaryText(chatcount + " new messages"))
                            //specify which group this notification belongs to
                            .setGroup(CHAT_GROUP_KEY)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent)
                            .build();
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                if(notificationManager!=null)
                    notificationManager.createNotificationChannel(channel);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
            }
            if(notificationManager!=null) {
                notificationManager.notify(num, builder.build());
                notificationManager.notify(SUMMARY_ID, summaryNotification);
            }
        }
    }
    public void checkCreator(String author,String title, String message,String dept)
    {
        //if(!author.equals(FirebaseAuth.getInstance().getUid()))
        //{
            if(dept == null) {
                sendEventNotification(title, message);
            }
            else
            {
                sendDeptNotification(title,message);
            }

        //}
    }
    public void sendEventNotification(String title, String message) {
        // Create an explicit intent for an Activity in your app
        count++;
        int SUMMARY_ID = 0;
        String EVENT_GROUP_KEY = "EVENT_GROUP";
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.cuhplogo);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
            CharSequence name = "CH01";
            String CHANNEL_ID = "CHID";
            String description = "BaseChannel";
            NotificationManager notificationManager;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.cuhplogo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setGroup(EVENT_GROUP_KEY)
                    .setLargeIcon(bmp)
                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            Notification summaryNotification =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("College Cobweb")
                            //set content text to support devices running API level < 24
                            .setContentText(count + " new events")
                            .setSmallIcon(R.drawable.cuhplogo)
                            .setDeleteIntent(getDeleteIntent())
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            //build summary info into InboxStyle template
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(title + " " + message)
                                    .addLine("Tap to check!")
                                    .setBigContentTitle(title)
                                    .setSummaryText(count + " new events"))
                            //specify which group this notification belongs to
                            .setGroup(EVENT_GROUP_KEY)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent)
                            .build();
            int num = (int) System.currentTimeMillis();
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
            if(notificationManager!=null)
                notificationManager.createNotificationChannel(channel);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
            }
            if(notificationManager!=null) {
                notificationManager.notify(num, builder.build());
                notificationManager.notify(SUMMARY_ID, summaryNotification);
            }
    }
    public void sendDeptNotification(String title, String message) {
        // Create an explicit intent for an Activity in your app
        deptcount++;
        int SUMMARY_ID = 1;
        String DEPT_EVENT_GROUP_KEY = "DEPT_EVENT_GROUP";
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.cuhplogo);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "CH01";
        String CHANNEL_ID = "CHID";
        String description = "Base Channel";
        NotificationManager notificationManager;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cuhplogo)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(bmp)
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message))
                .setGroup(DEPT_EVENT_GROUP_KEY)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification summaryNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("College Cobweb")
                        //set content text to support devices running API level < 24
                        .setContentText(deptcount+" new department events")
                        .setSmallIcon(R.drawable.cuhplogo)
                        .setColor(getResources().getColor(R.color.colorPrimaryDark))
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine(title + " " + message)
                                .addLine("....")
                                .setBigContentTitle(title)
                                .setSummaryText(deptcount + " new department events"))
                        //specify which group this notification belongs to
                        .setGroup(DEPT_EVENT_GROUP_KEY)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .setContentIntent(pendingIntent)
                        .build();
        int num = (int) System.currentTimeMillis();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            if(notificationManager!=null)
                notificationManager.createNotificationChannel(channel);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
        }
        if(notificationManager!=null) {
            notificationManager.notify(num, builder.build());
            notificationManager.notify(SUMMARY_ID, summaryNotification);
        }
    }
    protected PendingIntent getDeleteIntent()
    {
        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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