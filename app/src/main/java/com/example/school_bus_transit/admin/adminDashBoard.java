package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.SchoolAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class adminDashBoard extends AppCompatActivity {

    ConstraintLayout school;
    ProgressBar progressBar;
    LinearLayout allNumber;
    TextView schoolNo,busNo,driverNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
        progressBar = (ProgressBar)findViewById(R.id.data_loading);
        allNumber = (LinearLayout)findViewById(R.id.allNumber);
        schoolNo = findViewById(R.id.schoolNo);
        busNo = findViewById(R.id.busNo);
        driverNo = findViewById(R.id.driverNo);
        school = findViewById(R.id.goToSchool);
        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), viewAllSchools.class);
                startActivity(intent);
            }
        });
        getSchool();
        getBus();
        getDriver();
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
                            schoolNo.setText(String.valueOf(school.size()));
                        }
                    }
                }
        );



    }

    public void getBus()
    {
        FirebaseFirestore.getInstance().collection("Bus")
                .get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {



                            ArrayList<BusModel> bus = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                boolean as = (boolean) doc.getData().get("active_sharing");
                                boolean gs = (boolean) doc.getData().get("going_to_school");
                                bus.add(new BusModel(
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
                                ));
                            }
                            constants.allbus = bus;
                            busNo.setText(String.valueOf(bus.size()));
                        }
                    }
                }
        );



    }

    public void getDriver()
    {
        FirebaseFirestore.getInstance().collection("User")
                .whereEqualTo("user_type","DRIVER").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            ArrayList<UserModel> driver = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {

                                driver.add (new UserModel(
                                        doc.getData().get("user_id").toString(),
                                                doc.getData().get("photo_url").toString(),
                                                doc.getData().get("gender").toString(),
                                                doc.getData().get("bus_id").toString(),
                                                doc.getData().get("fullName").toString(),
                                                doc.getData().get("phone_no").toString(),
                                                new ArrayList<String>(),
                                        doc.getData().get("email_id").toString(),
                                        doc.getData().get("address").toString(),
                                        doc.getData().get("user_lat").toString(),
                                                doc.getData().get("user_long").toString(),
                                        doc.getData().get("user_type").toString()
                                        )

                                );
                            }
                            constants.alldriver = driver;
                            driverNo.setText(String.valueOf(driver.size()));
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        allNumber.setVisibility(View.VISIBLE);
                    }
                }
        );



    }
}