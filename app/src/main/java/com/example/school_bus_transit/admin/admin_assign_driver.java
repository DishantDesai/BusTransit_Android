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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.school_bus_transit.Login;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.assignDriverAdapter;
import com.example.school_bus_transit.adapter.busListAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class admin_assign_driver extends AppCompatActivity {

    RecyclerView recyclerViewDriverList;
    assignDriverAdapter assignAdapter;
    Context mContext;
    TextView school_name;
    TextView school_address;
    TextView end_address,driverCountHeading;
    Button deleteBusBtn;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assign_driver);
        mContext=getApplicationContext();
        fStore = FirebaseFirestore.getInstance();
        setData();
    }

    void setData()
    {
        school_name = (TextView) findViewById(R.id.admin_assign_driver_school_name) ;
        school_address = (TextView) findViewById(R.id.admin_assign_address_text) ;
        end_address = (TextView) findViewById(R.id.admin_assign_end_address_text) ;
        driverCountHeading = findViewById(R.id.admin_school_bus_heading);
        deleteBusBtn = findViewById(R.id.delete_school);
        school_name.setText(constants.CurrentSchool.getname());
        school_address.setText(constants.CurrentBus.getsource());
        end_address.setText(constants.CurrentBus.getdestination());

        List<UserModel> driver = new ArrayList<>();

        for(int i = 0; i< constants.alldriver.size(); i++)
        {
            if(constants.alldriver.get(i).getbus_id().equals(""))
            {
                driver.add(constants.alldriver.get(i));
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewDriverList =  findViewById(R.id.admin_assign_driver_recycle_view);
        recyclerViewDriverList.setVerticalScrollBarEnabled(false);
        driverCountHeading.setText("Driver List (" + driver.size() + ")");
        if(driver.size()!=0)
        {
            recyclerViewDriverList.setLayoutManager(linearLayoutManager);
            assignAdapter = new assignDriverAdapter(mContext, (ArrayList<UserModel>) driver);
            recyclerViewDriverList.setAdapter(assignAdapter);
            recyclerViewDriverList.setVisibility(View.VISIBLE);
        }


        deleteBusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBusHandler();
            }
        });
    }
    private void deleteBusHandler(){
        BusModel bus = constants.CurrentBus;

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete?");
        builder.setMessage("are you sure, you want to delete bus?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                fStore.collection("Bus").document(bus.getbus_id())
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
                                Toast.makeText(admin_assign_driver.this, "Delete Bus Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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