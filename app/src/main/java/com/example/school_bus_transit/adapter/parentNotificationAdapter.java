package com.example.school_bus_transit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Notification;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.model.NotificationModel;

import java.util.ArrayList;

public class parentNotificationAdapter extends RecyclerView.Adapter<parentNotificationAdapter.ViewHolder>
{

    ArrayList<NotificationModel> notificationModel;
    private Context context;

    public parentNotificationAdapter(Context context, ArrayList<NotificationModel> notificationModel) {
        this.notificationModel = notificationModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        System.out.println("parent "+ parent);
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_notification_sample, parent, false);
        return new ViewHolder (inflate);
    }

    public void updateData(ArrayList<NotificationModel> notificationModel)
    {
        this.notificationModel = notificationModel;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {

        int no = notificationModel.size() - position ;
        holder.title.setText("Notification "+no);
        holder.issue_heading.setText(notificationModel.get(position).getTitle().toString());
        holder.issue_data.setText(notificationModel.get(position).getMessage().toString());
        holder.time.setText(notificationModel.get(position).getTimestamp().toString());
    }
    @Override
    public int getItemCount() {
        return notificationModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,issue_heading,issue_data,time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time= itemView.findViewById(R.id.notification_time);
            title = itemView.findViewById(R.id.notification_no);
            issue_heading = itemView.findViewById(R.id.notification_issue);
            issue_data = itemView.findViewById(R.id.notification_data);
        }
    }
}
