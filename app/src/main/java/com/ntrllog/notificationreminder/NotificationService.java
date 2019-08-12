package com.ntrllog.notificationreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.gson.Gson;

import java.util.Map;

public class NotificationService extends JobIntentService {
    SharedPreferences savedNotifications;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        savedNotifications = getSharedPreferences("notifications", MODE_PRIVATE);
        createNotificationChannel();
        readFromGson();
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, 9, work);
    }

    public static void removeNotification(Context context, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Name";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void readFromGson() {
        Gson gson = new Gson();
        Map<String,?> keys = savedNotifications.getAll();

        /* Loop through existing Notifications to load when app starts */
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String json = entry.getValue().toString();
            Notification n = gson.fromJson(json, Notification.class);
            sendNotification(n.getContent(), n.getId());
        }
    }

    private void sendNotification(String s, int i) {
        /* Create intent so app opens when tapping notification*/
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Create notification in notification bar */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ID")
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setContentTitle("Notification Reminder")
                .setContentText(s)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(s+i)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(i, mBuilder.build());
    }
}
