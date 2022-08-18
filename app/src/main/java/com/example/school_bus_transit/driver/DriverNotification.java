package com.example.school_bus_transit.driver;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.school_bus_transit.MainActivity;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.Registration;
import com.example.school_bus_transit.helper.FirebaseHelper;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DriverNotification extends AppCompatActivity {

    TextInputEditText title_text_box,description_text_box;
    Button send_message;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_notification);

        title_text_box = findViewById(R.id.title_text_box);
        description_text_box = findViewById(R.id.description_text_box);

        send_message = findViewById(R.id.send_message);

        title_text_box.setText("Traffic jam test 1");
        description_text_box.setText("it will take more time to reach home!");

        send_message.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        CallDriverHomePage();

    }

    public void sendMessage(View view) {
        if(title_text_box.length() <= 3)
        {
            create_alert("Title message length must be more than 3");
            return;
        }
        if(description_text_box.length() <= 5)
        {
            create_alert("Title message length must be more than 5");
            return;
        }

        Map<String, Object> notification = new HashMap<>();
        notification.put("notification_id", "no id");
        notification.put("bus_id", constants.CurrentBus.getbus_id());
        notification.put("driver_id", constants.CurrentUser.getuser_id());
        notification.put("school_id", constants.CurrentBus.getschool_id());
        notification.put("title", title_text_box.getText().toString());
        notification.put("message", description_text_box.getText().toString());
        notification.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("Notification")
                .add(notification)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        if(FirebaseHelper.updateField("Notification",documentReference.getId(),"notification_id",documentReference.getId())){
                            Log.d(TAG, "notification ID: " + documentReference.getId() + " -- Updated");
                        }
                        else
                        {
                            Log.d(TAG, "Error in : notification ID: " + documentReference.getId());
                        }

                        title_text_box.setText("");
                        description_text_box.setText("");
                        Toast.makeText(DriverNotification.this, "Notification sent successfully !!! ", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });




    }

    public void create_alert(String msg)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
//                alertDialogBuilder.setPositiveButton("yes",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0, int arg1) {
////                                Toast.makeText(this,"You clicked yes button",Toast.LENGTH_LONG).show();
//                            }
//                        });

//        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void CallDriverHomePage()
    {

        FirebaseFirestore.getInstance().collection("Bus").document(constants.CurrentUser.getbus_id().trim()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful())
                                               {
                                                   DocumentSnapshot doc = task.getResult();

                                                   boolean as = (boolean) doc.getData().get("active_sharing");
                                                   boolean gs = (boolean) doc.getData().get("going_to_school");
                                                   constants.CurrentBus = new BusModel(
                                                           as,
                                                           gs,
                                                           doc.getData().get("bus_id").toString(),
                                                           doc.getData().get("bus_number").toString(),
                                                           doc.getData().get("current_lat").toString(),
                                                           doc.getData().get("current_long").toString(),
                                                           doc.getData().get("destination").toString(),
                                                           doc.getData().get("destination_lat").toString(),
                                                           doc.getData().get("destination_long").toString(),
                                                           doc.getData().get("school_id").toString(),
                                                           doc.getData().get("source").toString(),
                                                           doc.getData().get("source_lat").toString(),
                                                           doc.getData().get("source_long").toString()
                                                   );

                                               }

                                           }
                                       }
                );

    }


}

//  0u1vnkBitgzbs4ENtrYr
//  0u1vnkBitgzbs4ENtrYr
