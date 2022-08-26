package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.SchoolAdapter;
import com.example.school_bus_transit.adapter.busListAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    Button deleteSchoolBtn;
    FirebaseFirestore fStore;

    MaterialToolbar topbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_details);
        getSupportActionBar().hide();

        fStore = FirebaseFirestore.getInstance();

        String school_id = (String) getIntent().getSerializableExtra("school_id");
        mContext=getApplicationContext();

        topAppBar = (MaterialToolbar)findViewById(R.id.school_detail_topbar);
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
            String schoolId = school_id != null ? school_id : constants.CurrentSchool.getschool_id();
            if(schoolId.equals(constants.allschool.get(i).getschool_id()))
            {
                school = constants.allschool.get(i);
            }
        }

        schoolName = findViewById(R.id.admin_school_schoolName);
        schoolEmail = findViewById(R.id.schoolEmailVal);
        schoolMo = findViewById(R.id.schoolMobVal);
        schoolAddress = findViewById(R.id.schoolAddressVal);
        busHeading = findViewById(R.id.admin_school_bus_heading);
        deleteSchoolBtn = findViewById(R.id.delete_school);


        deleteSchoolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSchoolHandler();
            }
        });
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
        busHeading.setText("Bus List (" + bus.size() + ")");

        //Set recycle view adapter for bus list
        recyclerViewSchoolList.setLayoutManager(linearLayoutManager);
        busAdapter = new busListAdapter(mContext, (ArrayList<BusModel>) bus);
        recyclerViewSchoolList.setAdapter(busAdapter);
        recyclerViewSchoolList.setVisibility(View.VISIBLE);
//        else
//        {
//            busHeading.setVisibility(View.INVISIBLE);
//        }


    }

    private void deleteSchoolHandler(){


        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete School?");
        builder.setMessage("are you sure, you want to delete school?");

        /*
        * Delete School
        *   -> Check if school has bus assigned
        *       -> Yes, ask admin to delete all bus first
        *       -> No, Delete School
        * */

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                //Query to found all buses for school
                fStore.collection("Bus").whereEqualTo("school_id",school.getschool_id())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    //If bus found then ask to delete all bus
                                    if(task.getResult().size() > 0){
                                        Toast.makeText(schoolDetails.this, "Attention: Inorder to delete school you need to delete all Buses!" , Toast.LENGTH_LONG).show();
                                    }else{// Delete school
                                        fStore.collection("School")
                                                .document(school.getschool_id())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(schoolDetails.this, "Delete School Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}