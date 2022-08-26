package com.example.school_bus_transit.parents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.parentNotificationAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.NotificationModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParentNotification extends AppCompatActivity {

    Context context;
    ArrayList<NotificationModel> notificationModelData =new ArrayList<>();
    RecyclerView recyclerViewNotificationList;
    private RecyclerView.Adapter notificationAdapter;
    MaterialToolbar topbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_parent_notification);
        getNotification();
        topbar = findViewById(R.id.ParentNotification_topAppBar);

        topbar.setTitle("Welcome " + constants.CurrentUser.getfullName());
    }

    public void getNotification()
    {

        List<String> school_id = new ArrayList<>();

        for(int i = 0; i< constants.allschool.size(); i++)
        {
            if(constants.CurrentUser.getschool_id().contains(constants.allschool.get(i).getname()))
            {
                school_id.add(constants.allschool.get(i).getschool_id());
            }
        }


        FirebaseFirestore.getInstance().collection("Notification").whereIn("school_id",school_id).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        notificationModelData.clear();


//                        String tittle="aa";
//                        String subject="aaaaa";
//                        String body="abc";
//
//                        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                        Notification notify=new Notification.Builder
//                                (getApplicationContext()).setContentTitle(tittle).setContentText(body).
//                                setContentTitle(subject).setSmallIcon(R.drawable.add_bus).build();
//
//                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
//                        notif.notify(0, notify);



                        for(DocumentSnapshot doc : value.getDocuments())
                        {
                            com.google.firebase.Timestamp t= (com.google.firebase.Timestamp) doc.getData().get("timestamp");
                            Date d = t.toDate();
                            notificationModelData.add (new NotificationModel(doc.getData().get("notification_id").toString(),
                                            doc.getData().get("bus_id").toString(),
                                            doc.getData().get("driver_id").toString(),
                                            doc.getData().get("school_id").toString(),
                                            doc.getData().get("title").toString(),
                                            doc.getData().get("message").toString(),
                                            d
                                    )

                            );

                        }

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                        recyclerViewNotificationList =  findViewById(R.id.ParentNotification_recycle_view);

                        if(notificationModelData.size()!=0)
                        {
                            recyclerViewNotificationList.setLayoutManager(linearLayoutManager);
                            notificationAdapter = new parentNotificationAdapter(context, (ArrayList<NotificationModel>) notificationModelData);
                            recyclerViewNotificationList.setAdapter(notificationAdapter);
                            recyclerViewNotificationList.setVisibility(View.VISIBLE);
                        }

                        ProgressBar loader = (ProgressBar)findViewById(R.id.parent_notification_data_loading);
                        loader.setVisibility(View.INVISIBLE);
                    }
                });


    }
}