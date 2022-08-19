package com.example.school_bus_transit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.SchoolModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSchool();
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        },2000);
    }

    public void getSchool()
    {
        FirebaseFirestore.getInstance().collection("School")
                .get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {

                                    ArrayList<SchoolModel> school = new ArrayList<>();
                                    for(QueryDocumentSnapshot doc : task.getResult())
                                    {
                                        school.add (new SchoolModel(doc.getData().get("name").toString(),
                                                        doc.getData().get("phone_no").toString(),
                                                        doc.getData().get("school_id").toString(),
                                                        doc.getData().get("email_id").toString(),
                                                        doc.getData().get("address").toString(),
                                                        doc.getData().get("lat").toString(),
                                                        doc.getData().get("long").toString()
                                                )

                                        );
                                    }
                                    constants.allschool = school;
                                }
                            }
                        }
                );

    }
}