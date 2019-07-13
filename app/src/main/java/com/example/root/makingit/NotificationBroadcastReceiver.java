package com.example.root.makingit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("notification_cancelled")) {
            MyFirebaseMessagingService.count = 0;
            MyFirebaseMessagingService.deptcount = 0;
            MyFirebaseMessagingService.chatcount =0;
        }
    }
}
