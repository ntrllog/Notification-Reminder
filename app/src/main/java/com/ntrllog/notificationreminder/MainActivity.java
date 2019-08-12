package com.ntrllog.notificationreminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final ArrayList<Notification> notificationArrayList = new ArrayList<>();
    NotificationAdapter adapter;
    ListView listView;
    int id = 0; // for saving unique Notifications to Shared Preferences
    SharedPreferences savedNotifications;
    SharedPreferences idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedNotifications = getSharedPreferences("notifications", MODE_PRIVATE);
        idList = getSharedPreferences("id", MODE_PRIVATE);
        adapter = new NotificationAdapter(this, notificationArrayList);
        listView = findViewById(R.id.list);
        registerForContextMenu(listView);

        updateAdapter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = idList.getInt("notification_id_key", 0);
                notificationArrayList.add(new Notification("Tap To Edit/Hold To Delete", id));
                idList.edit().putInt("notification_id_key", (id+1) % Integer.MAX_VALUE).apply();
                listView.setAdapter(adapter);
            }
        });

        /* Show notifications in notification bar */
        Intent notificationServiceIntent = new Intent(this, NotificationService.class);
        NotificationService.enqueueWork(getApplicationContext(), notificationServiceIntent);
    }

    private void updateAdapter() {
        /* Loop through Notifications Shared Preferences to update UI */
        Gson gson = new Gson();
        Map<String,?> keys = savedNotifications.getAll();
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String json = entry.getValue().toString();
            Notification n = gson.fromJson(json, Notification.class);
            notificationArrayList.add(n);
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Notification n = notificationArrayList.get(position);
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

                        /* Show notifications in notification bar */
                        Intent notificationServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
                        NotificationService.enqueueWork(getApplicationContext(), notificationServiceIntent);
                    }
                });
                c.show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        /* Remove from Shared Preferences */
        Notification n = notificationArrayList.get(info.position);
        SharedPreferences.Editor prefsEditor = savedNotifications.edit();
        prefsEditor.remove(""+n.getId());
        prefsEditor.apply();

        /* Remove from notification bar */
        NotificationService.removeNotification(getApplicationContext(), n.getId());

        /* Remove from UI */
        notificationArrayList.remove(info.position);
        listView.setAdapter(adapter);

        return true;
    }
}
