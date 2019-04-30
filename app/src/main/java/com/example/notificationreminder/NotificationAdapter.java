package com.example.notificationreminder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    NotificationAdapter(Activity context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate (R.layout.list_item, parent, false);
        }

        Notification currentNotification = getItem(position);

        TextView content = listItemView.findViewById (R.id.content);
        content.setText(currentNotification.getContent());

        return listItemView;
    }
}
