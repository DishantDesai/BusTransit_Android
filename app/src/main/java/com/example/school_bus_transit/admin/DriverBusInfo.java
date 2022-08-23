package com.example.school_bus_transit.admin;

import static com.google.android.gms.tasks.Tasks.await;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.helper.DirectionsJSONParser;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.helper.fetchRoute;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import com.google.android.gms.common.internal.Constants;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DriverBusInfo extends AppCompatActivity  implements OnMapReadyCallback,GoogleMap.OnCameraMoveStartedListener {

    TextView schoolName,schoolPhone,schoolAddress,endAddress,driverName,tripStatus,tripDirection, busno;
    Button releaseDriver;
    ShapeableImageView driverProfile;
    UserModel d;
    BusModel b;
    SchoolModel s;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Button rDriver;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    ScrollView parenScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bus_info);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        parenScrollView = findViewById(R.id.map_parent_scroll_view);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getActionBar();
        setData();
//
        getdata();



        //Firebase Object Initialisation
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        rDriver=findViewById(R.id.relese_driver);

        rDriver.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Map<String,Object> school_update = new HashMap<>();
                school_update.put("bus_id","");
                fStore.collection("User").document(d.getuser_id()).update(school_update);
                finish();
            }
        });


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Your code to run in GUI thread here
                        onMapReady(mMap);
                    }
                });

            }
        }, 12000);

    }

    void setData()
    {
        String driver_id = (String) getIntent().getSerializableExtra("driver_id");

        driverName = (TextView) findViewById(R.id.admin_school_info_driver_name);
        driverProfile = findViewById(R.id.driver_info_profile_image);
        tripStatus = (TextView) findViewById(R.id.trip_status);
        tripDirection = (TextView) findViewById(R.id.direction);
        schoolName = (TextView) findViewById(R.id.admin_school_info_name);
        schoolAddress = (TextView) findViewById(R.id.school_address_text);
        endAddress = (TextView) findViewById(R.id.end_address_text);
        busno = (TextView) findViewById(R.id.admin_school_info_bus_no);


        for(int i=0;i< constants.alldriver.size();i++)
        {
            if(driver_id.equals(constants.alldriver.get(i).getuser_id()))
            {
                d = constants.alldriver.get(i);
            }
        }
        String busid= d.getbus_id();

        for(int i=0;i< constants.allbus.size();i++)
        {
            if(busid.equals(constants.allbus.get(i).getbus_id()))
            {
                b = constants.allbus.get(i);
            }
        }

        for(int i=0;i< constants.allschool.size();i++)
        {
            if(b.getschool_id().equals(constants.allschool.get(i).getschool_id()))
            {
                s = constants.allschool.get(i);
            }
        }

        driverName.setText(d.getfullName());
        Glide.with(this)
                .load(d.getphoto_url())
                .into(driverProfile);
        if(b.getactive_sharing())
        {
            tripStatus.setText("On");
            if(b.getgoing_to_school())
            {
                tripDirection.setText("Going to School");
            }
            else
                tripDirection.setText("Going from School");

        }else{
            tripStatus.setText("Off");
            tripDirection.setText("-");
        }

        schoolName.setText(s.getname());
        schoolAddress.setText(s.getaddress());
        endAddress.setText(b.getdestination());
        busno.setText("Bus Number : "+b.getbus_number());


    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setOnCameraMoveStartedListener(this);
        BitmapDescriptor logo = BitmapDescriptorFactory.fromResource(R.drawable.logo1);
        // Add a marker in Sydney and move the camera
//        LatLng curr = new LatLng(Double.parseDouble(b.getcurrent_lat()), Double.parseDouble(b.getcurrent_long()));

        LatLng curr = new LatLng(Double.parseDouble(b.getcurrent_lat()), Double.parseDouble(b.getcurrent_long()));
        LatLng des = new LatLng(Double.parseDouble(b.getdestination_lat()), Double.parseDouble(b.getdestination_long()));
        LatLng source = new LatLng(Double.parseDouble(b.getsource_lat()), Double.parseDouble(b.getsource_long()));

        //Map control settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(des).title(b.getdestination()));
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(source).title(b.getsource()));


        if(b.getactive_sharing()){

            mMap.addMarker(new MarkerOptions().icon(logo).position(curr).title("Going to "+b.getdestination()));
            //       find all latlong of shortesh path and add into list and uncomment below lines ,  path will be ready in map
            PolylineOptions routeCoordinates = new PolylineOptions();
            if(constants.routes.size()!=0)
            {
                for (HashMap<String, String> latLng : constants.routes.get(0)) {
                    routeCoordinates.add(new LatLng(Double.parseDouble(latLng.get("lat")), Double.parseDouble(latLng.get("lng"))));
                }
                routeCoordinates.width(10);
                routeCoordinates.color(Color.BLUE);
                Polyline route = mMap.addPolyline(routeCoordinates);
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 25));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(des));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(25), 2000, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(des )      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private String getMapsApiDirectionsUrl() {
        String str_origin = "origin=" + b.getsource_lat() + "," + b.getsource_long();
        String str_dest = "destination=" + b.getdestination_lat() + "," + b.getdestination_long();
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + "driving" + "&alternatives=true"
                +"&key="+"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private void getdata() {

        FirebaseFirestore.getInstance().collection("Bus").whereEqualTo("bus_id",b.getbus_id()).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for(DocumentSnapshot doc : value.getDocuments())
                        {
                            boolean activeSharing = (boolean) doc.getData().get("active_sharing");
                            boolean goingToToSchool = (boolean) doc.getData().get("going_to_school");

                            constants.allbus.remove(b);
                            b =new BusModel(
                                    activeSharing,
                                    goingToToSchool,
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
                            );
                            constants.allbus.add(b);

                            fetchRoute myAsyncTasks = new fetchRoute();
                            myAsyncTasks.execute(getMapsApiDirectionsUrl(),"","");

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Your code to run in GUI thread here
                                onMapReady(mMap);
                            }
                        });
                    }
                });



    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            parenScrollView.requestDisallowInterceptTouchEvent(true);

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            parenScrollView.requestDisallowInterceptTouchEvent(true);

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            parenScrollView.requestDisallowInterceptTouchEvent(true);

        }
    }
}