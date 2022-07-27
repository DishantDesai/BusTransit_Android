package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.SchoolAdapter;
import com.example.school_bus_transit.adapter.busListAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class schoolDetails extends AppCompatActivity implements Serializable
{
    SchoolModel school ;
    TextView schoolName , schoolEmail , schoolMo , schoolAddress  , busHeading;
    private RecyclerView recyclerViewSchoolList;
    private RecyclerView.Adapter busAdapter;
    static Context mContext;
    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_details);
        getSupportActionBar().hide();

        String school_id = (String) getIntent().getSerializableExtra("school_id");
        mContext=getApplicationContext();

        topAppBar = (MaterialToolbar)findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.add_bus_icon:

                        Intent intent = new Intent(mContext,add_new_bus.class);
                        intent.putExtra("school_id",school.getschool_id());
                        intent.putExtra("school_address",school.getaddress());
                        intent.putExtra("school_lat",school.getlat());
                        intent.putExtra("school_long",school.getlong());

                        startActivity(intent);
                        break;
                    // TODO: Other cases
                }
                return true;
            }
        });


        for(int i=0;i<constants.allschool.size();i++)
        {
            if(school_id.equals(constants.allschool.get(i).getschool_id()))
            {
                school = constants.allschool.get(i);
            }
        }

        schoolName = findViewById(R.id.admin_school_schoolName);
        schoolEmail = findViewById(R.id.schoolEmailVal);
        schoolMo = findViewById(R.id.schoolMobVal);
        schoolAddress = findViewById(R.id.schoolAddressVal);
        busHeading = findViewById(R.id.admin_school_bus_heading);

//        getBus();
        setData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getBus();
        getDriver();
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
                                    setData();
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
                                }
                            }
                        }
                );



    }

    void setData()
    {
        List<BusModel> bus = new ArrayList<>();
        schoolName.setText(school.getname());
        schoolEmail.setText(school.getemail_id());
        schoolMo.setText(school.getphone_no());
        schoolAddress.setText(school.getaddress());

        for(int i=0;i<constants.allbus.size();i++)
        {
            if(constants.allbus.get(i).getschool_id().equals(school.getschool_id()))
            {
                bus.add(constants.allbus.get(i));
            }
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewSchoolList =  findViewById(R.id.admin_bus_list_recycle_view);
        recyclerViewSchoolList.setVerticalScrollBarEnabled(false);
        if(bus.size()!=0)
        {
            recyclerViewSchoolList.setLayoutManager(linearLayoutManager);
            busAdapter = new busListAdapter(mContext, (ArrayList<BusModel>) bus);
            recyclerViewSchoolList.setAdapter(busAdapter);
            recyclerViewSchoolList.setVisibility(View.VISIBLE);
        }
        else
        {
            busHeading.setVisibility(View.INVISIBLE);
        }


    }


}