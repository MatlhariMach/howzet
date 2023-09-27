package com.example.howzit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.parse.ParsePushBroadcastReceiver;

public class MyPushReceiver extends ParsePushBroadcastReceiver {

    private static final String CHANNEL_ID = "MyChannelId";
    private static final int NOTIFICATION_ID = 123;

    @Override
    public void onPushReceive(Context context, Intent intent) {
        // Handle incoming push notification (optional)
        // In this example, we will show a simple notification without any custom actions
        String message = intent.getStringExtra("alert");
        showNotification(context, message);
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        // Handle notification click action (optional)
        // In this example, we will simply launch the MainActivity when the notification is clicked.
        Intent launchIntent = new Intent(context, MainActivity2.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }

    private void showNotification(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_social_notifications)
                .setContentTitle("My App")
                .setContentText(message)
                .setAutoCancel(true);

        // Set the notification click action (optional)
        Intent intent = new Intent(context, MainActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

