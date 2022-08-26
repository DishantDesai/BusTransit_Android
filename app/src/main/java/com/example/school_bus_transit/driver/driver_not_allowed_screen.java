package com.example.school_bus_transit.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.school_bus_transit.Login;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.admin.adminDashBoard;
import com.example.school_bus_transit.helper.constants;
import com.google.android.material.appbar.MaterialToolbar;

public class driver_not_allowed_screen extends AppCompatActivity {

    private Button logoutBtn;
    MaterialToolbar topbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver_not_allowed_screen);
        logoutBtn=findViewById(R.id.driver_logout);
        topbar = findViewById(R.id.no_bus_assigned_topbar);
        topbar.setTitle("Welcome " + constants.CurrentUser.getfullName());
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(driver_not_allowed_screen.this,Login.class ));
                Toast.makeText(driver_not_allowed_screen.this, "User logged out successfully !!! " , Toast.LENGTH_SHORT).show();
            }
        });
    }
}