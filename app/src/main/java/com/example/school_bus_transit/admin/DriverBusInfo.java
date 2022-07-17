package com.example.school_bus_transit.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.school_bus_transit.R;

public class DriverBusInfo extends AppCompatActivity {

    TextView schoolName,schoolPhone,schoolAddress,endAddress,driverName,tripStatus,tripDirection;
    Button releaseDriver;
    ImageView driverProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bus_info);
        getActionBar();


    }
}