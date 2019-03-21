package com.example.notificationreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
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
    int id = 0;
    SharedPreferences savedNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        adapter = new NotificationAdapter(this, notifications);
        savedNotifications = getSharedPreferences("notifications", MODE_PRIVATE);

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

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Notification n = notifications.get(position);
                CustomDialog c = new CustomDialog(MainActivity.this);
                c.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(String result) {
                        n.setContent(result);
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

        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String json = entry.toString().substring(2);
            Notification n = gson.fromJson(json, Notification.class);
            notifications.add(n);
            id = n.getId();
        }
    }

    private void sendNotification(String s, int i) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ID")
                .setSmallIcon(R.drawable.icons8_android_512)
                .setContentTitle("Notification Reminder")
                .setContentText(s)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
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
        notifications.add(new Notification("A", ++id));
        updateAdapter();
        return false;
    }

}
