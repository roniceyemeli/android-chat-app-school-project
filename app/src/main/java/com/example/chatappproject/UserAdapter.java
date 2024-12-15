package com.example.chatappproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import models.UserModel;

public class UserAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<UserModel> users;

    public UserAdapter(Context context, ArrayList<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        }

        UserModel user = users.get(position);

        ImageView avatar = convertView.findViewById(R.id.userAvatar);
        TextView name = convertView.findViewById(R.id.userName);


        avatar.setImageResource(R.drawable.user);
        name.setText(user.getUserName());

        return convertView;
    }
}
