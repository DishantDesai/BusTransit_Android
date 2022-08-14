package com.example.school_bus_transit.parents;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.school_bus_transit.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class parentDriverProfile extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo;
    EditText address;
    RadioGroup gender;
    ShapeableImageView profileImage;
    String[] schoolArr = {"Concordia University","John Abbot","Vanier"};
    ArrayList<Integer> schoolsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_driver_profile);
        getSupportActionBar();

        profileImage = findViewById(R.id.parent_driver_image);
        email = findViewById(R.id.parent_driver_email);
        fullName = findViewById(R.id.parent_driver_full_name);
        phoneNo = findViewById(R.id.parent_driver_phone_no);


    }
}