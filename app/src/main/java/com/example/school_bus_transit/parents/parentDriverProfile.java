package com.example.school_bus_transit.parents;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.helper.constants;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class parentDriverProfile extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo;
    TextView gender;
    EditText address;
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
        gender = (TextView) findViewById(R.id.parent_driver_gender);

        email.getEditText().setText(constants.CurrentSelectedDriver.getemail_id());
        fullName.getEditText().setText(constants.CurrentSelectedDriver.getfullName());
        phoneNo.getEditText().setText(constants.CurrentSelectedDriver.getphone_no());
        gender.setText(constants.CurrentSelectedDriver.getgender());
        Glide.with(this)
                .load(constants.CurrentSelectedDriver.getphoto_url())
                .into(profileImage);
    }
}