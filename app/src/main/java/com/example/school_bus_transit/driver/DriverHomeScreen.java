package com.example.school_bus_transit.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.school_bus_transit.Login;
import com.example.school_bus_transit.MapFragment;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.helper.FirebaseHelper;
import com.example.school_bus_transit.helper.GPSTracker;
import com.example.school_bus_transit.helper.LocationGetter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.helper.fetchRoute;
import com.example.school_bus_transit.model.BusModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DriverHomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener//, OnMapReadyCallback
{

    Button tripFromSchool, tripToSchool, stopTrip;
    View userIcon;

    //to Show Driver Location
    private GoogleMap mMap;
    Fragment fragment;

    LatLng des;
    LatLng source;

    //firebase operation
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    BottomNavigationView bottomNav ;
    GPSTracker locationHelper;
    MaterialToolbar topbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home_screen);
        getSupportActionBar().hide();
        //firebase object initialize

        tripFromSchool = findViewById(R.id.driverTripFromSchool);
        tripToSchool = findViewById(R.id.driverTriptoSchool);
        stopTrip = findViewById(R.id.stopTrip);

        // Initialize fragment
        fragment=new MapFragment(tripFromSchool, tripToSchool, stopTrip,this);
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.DriverMap,fragment)
                .commit();


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        stopTrip.setActivated(false);
        bottomNav = findViewById(R.id.driver_bottomNavigationView);


        topbar = findViewById(R.id.driver_home_topbar);
        topbar.setTitle("Welcome " + constants.CurrentUser.getfullName());

        userIcon = findViewById(R.id.add_user_icon);


        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);


        getActionBar();


    }

    public void showAlertDialogButtonClicked(View view) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out");
        builder.setMessage("are you sure want to sign out?");

        // add the buttons
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
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

    @Override
    public void onResume() {
        super.onResume();
//        bottomNav.setSelectedItemId(R.id.bottom_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_profile:
                startActivity(new Intent(DriverHomeScreen.this, DriverProfile.class));
                return true;

            case R.id.bottom_home:
//                startActivity(new Intent(DriverHomeScreen.this, DriverHomeScreen.class));
                return true;

            case R.id.bottom_notification:
                startActivity(new Intent(DriverHomeScreen.this, DriverNotification.class));
                return true;
        }
        return false;
    }






}