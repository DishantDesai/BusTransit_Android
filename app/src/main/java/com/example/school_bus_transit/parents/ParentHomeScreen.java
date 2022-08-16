package com.example.school_bus_transit.parents;

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
import android.widget.ProgressBar;

import com.example.school_bus_transit.Login;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.Registration;
import com.example.school_bus_transit.adapter.busListAdapter;
import com.example.school_bus_transit.adapter.parentHomeScreenAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import com.example.school_bus_transit.model.parentScreenModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.OnSelectionChangedListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ParentHomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    RecyclerView recyclerViewBusList;
    private RecyclerView.Adapter busAdapter;
    NavigationView bottomNavigationView;


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_profile:
                startActivity(new Intent(ParentHomeScreen.this, parentProfile.class));
                return true;

            case R.id.bottom_home:
                startActivity(new Intent(ParentHomeScreen.this, ParentHomeScreen.class));
                return true;

            case R.id.bottom_notification:
                startActivity(new Intent(ParentHomeScreen.this, ParentNotification.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        context = getApplicationContext();
//        setupBottomNavigation();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_home_screen);
        getSupportActionBar().hide();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        List<String> school_id = new ArrayList<String>();

        for(int i=0;i<constants.allschool.size();i++)
        {
            if(constants.CurrentUser.getschool_id().contains(constants.allschool.get(i).getname()))
            {
                school_id.add(constants.allschool.get(i).getschool_id());
            }
        }

        getParentData(school_id);


    }


    public void getParentData(List<String> school_id)
    {

        ArrayList<BusModel> bus = new ArrayList<>();
        ArrayList<UserModel> driver = new ArrayList<>();
        ArrayList<parentScreenModel> parentScreendata = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Bus").whereIn("school_id",school_id)
                .get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {

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

                                }
                            }
                        }
                );


        FirebaseFirestore.getInstance().collection("User")
                .whereEqualTo("user_type","DRIVER").get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {

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
                                    for(int i=0;i<bus.size();i++)
                                    {
                                        for(int j=0;j<driver.size();j++)
                                        {
                                            if(bus.get(i).getbus_id().equals(driver.get(j).getbus_id()))
                                            {
                                                parentScreendata.add(new parentScreenModel(
                                                                driver.get(j).getfullName(),
                                                                driver.get(j).getphone_no(),
                                                                driver.get(j).getbus_id(),
                                                                driver.get(j).getphoto_url(),
                                                                bus.get(i).getbus_number(),
                                                                bus.get(i).getgoing_to_school()
                                                        )

                                                );
                                            }
                                        }
                                    }

                                    constants.parentScreendata = parentScreendata;


                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
                                    recyclerViewBusList =  findViewById(R.id.parent_homeScreen_recycle_view);

                                    if(constants.parentScreendata.size()!=0)
                                    {
                                        recyclerViewBusList.setLayoutManager(linearLayoutManager);
                                        busAdapter = new parentHomeScreenAdapter(context, (ArrayList<parentScreenModel>) constants.parentScreendata);
                                        recyclerViewBusList.setAdapter(busAdapter);
                                        recyclerViewBusList.setVisibility(View.VISIBLE);
                                    }
//                                    startActivity(new Intent(ParentHomeScreen.this, parentProfile.class));
                                    ProgressBar loader = (ProgressBar)findViewById(R.id.parent_data_loading);
                                    loader.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                );





    }


}