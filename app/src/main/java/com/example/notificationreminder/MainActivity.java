package com.example.notificationreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final ArrayList<Notification> notifications = new ArrayList<>();
    NotificationAdapter adapter;
    ListView listView;
    int id = 0; // for saving unique Notifications to Shared Preferences
    SharedPreferences savedNotifications;
    SharedPreferences idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        adapter = new NotificationAdapter(this, notifications);
        savedNotifications = getSharedPreferences("notifications", MODE_PRIVATE);
        idList = getSharedPreferences("id", MODE_PRIVATE);
        listView = findViewById(R.id.list);

        registerForContextMenu(listView);

        readFromGson();
        updateAdapter();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Name";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateAdapter() {
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Notification n = notifications.get(position);
                CustomDialog c = new CustomDialog(MainActivity.this, n.getContent());
                c.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(String result) {
                        n.setContent(result);

                        /* Save to Shared Preferences */
                        SharedPreferences.Editor prefsEditor = savedNotifications.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(n);
                        prefsEditor.putString(""+n.getId(), json);
                        prefsEditor.apply();

                        sendNotification(result, n.getId());
                    }
                });
                c.show();
            }
        });
    }

    private void readFromGson() {
        Gson gson = new Gson();
        Map<String,?> keys = savedNotifications.getAll();

        /* Loop through existing Notifications to load when app starts */
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String json = entry.toString().substring(2);
            Notification n = gson.fromJson(json, Notification.class);
            notifications.add(n);
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

        /* Create actual notification */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ID")
                .setSmallIcon(R.drawable.icons8_android_512)
                .setContentTitle("Notification Reminder")
                .setContentText(s)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(i, mBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        id = idList.getInt("notification_id_key", 0);
        notifications.add(new Notification("Tap To Edit/Hold To Delete", id));
        idList.edit().putInt("notification_id_key", (id+1) % Integer.MAX_VALUE).apply();
        updateAdapter();
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        /* Remove from Shared Preferences */
        Notification n = notifications.get(info.position);
        SharedPreferences.Editor prefsEditor = savedNotifications.edit();
        prefsEditor.remove(""+n.getId());
        prefsEditor.apply();

        /* Remove from notification bar */
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(n.getId());

        /* Remove from ArrayList (to remove from ListView) */
        notifications.remove(info.position);
        updateAdapter();

        return true;
    }

}
