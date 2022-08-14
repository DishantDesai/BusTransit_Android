package com.example.school_bus_transit.parents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.parentHomeScreenAdapter;
import com.example.school_bus_transit.adapter.parentNotificationAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.Notification;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.parentScreenModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ParentNotification extends AppCompatActivity {

    Context context;
    ArrayList<Notification> notificationData;
    RecyclerView recyclerViewNotificationList;
    private RecyclerView.Adapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_notification);
    }

    public void getNotification()
    {

        FirebaseFirestore.getInstance().collection("Notification")
                .get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {

                                    for(QueryDocumentSnapshot doc : task.getResult())
                                    {
                                        notificationData.add (new Notification(doc.getData().get("notification_id").toString(),
                                                        doc.getData().get("bus_id").toString(),
                                                        doc.getData().get("driver_id").toString(),
                                                        doc.getData().get("school_id").toString(),
                                                        doc.getData().get("title").toString(),
                                                        doc.getData().get("message").toString(),
                                                        (Date) doc.getData().get("timestamp")
                                                )

                                        );
                                    }

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                                    recyclerViewNotificationList =  findViewById(R.id.ParentNotification_recycle_view);

                                    if(notificationData.size()!=0)
                                    {
                                        recyclerViewNotificationList.setLayoutManager(linearLayoutManager);
                                        notificationAdapter = new parentNotificationAdapter(context, (ArrayList<Notification>) notificationData);
                                        recyclerViewNotificationList.setAdapter(notificationAdapter);
                                        recyclerViewNotificationList.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                );

    }
}