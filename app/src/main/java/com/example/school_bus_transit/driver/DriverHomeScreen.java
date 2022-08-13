package com.example.school_bus_transit.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DriverHomeScreen extends AppCompatActivity implements OnMapReadyCallback {

    Button tripFromSchool, tripToSchool, stopTrip;

    //to Show Driver Location
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    //firebase operation
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    GPSTracker locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home_screen);
        getSupportActionBar().hide();
        //firebase object initialize
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        tripFromSchool = findViewById(R.id.driverTripFromSchool);
        tripToSchool = findViewById(R.id.driverTriptoSchool);
        stopTrip = findViewById(R.id.stopTrip);
        stopTrip.setActivated(false);


        locationHelper  = new GPSTracker(this, new LocationGetter() {
            @Override
            public void LocationChangeListner(double latitude, double longitude) {
                LocationChange(latitude,longitude);

                if(constants.CurrentBus!=null)
                {
                    fetchRoute myAsyncTasks = new fetchRoute();
                    myAsyncTasks.execute(getMapsApiDirectionsUrl(),"","");

                    onMapReady(mMap);
                }
            }
        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.DriverMap);
        mapFragment.getMapAsync(this);

        getActionBar();


        System.out.println("hello");
        tripFromSchool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_tripFromSchool(v);
            }
        });

        tripToSchool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_tripToSchool(v);
            }
        });

        stopTrip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_stopTrip(v);
            }
        });

    }

    String getMapsApiDirectionsUrl() {
        String str_origin = "origin=" + constants.CurrentBus.getcurrent_lat()+ "," + constants.CurrentBus.getcurrent_long();
        String str_dest = "destination=" + constants.CurrentBus.getdestination_lat() + "," + constants.CurrentBus.getdestination_long();
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + "driving" + "&alternatives=true"
                +"&key="+"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
    public void LocationChange(double latitude, double longitude)
    {

                constants.CurrentBus.setcurrent_lat(String.valueOf(latitude));
                constants.CurrentBus.setcurrent_long(String.valueOf(longitude));

                onMapReady(mMap);

                FirebaseHelper.update2Field("Bus", constants.CurrentBus.getbus_id(),
                        "current_lat", String.valueOf(latitude),
                        "current_long", String.valueOf(longitude));
//                Toast.makeText(this, latitude + " -  VVV - " + longitude, Toast.LENGTH_SHORT).show();
    }


    public void start_tripFromSchool(View view) {

        try {

            if (!constants.CurrentBus.getactive_sharing()) {

                Map<String, Object> bus_update = new HashMap<>();
                bus_update.put("going_to_school", false);
                bus_update.put("active_sharing", true);
                fStore.collection("Bus").document(constants.CurrentBus.getbus_id()).update(bus_update);
                Toast.makeText(this, "Trip started to School", Toast.LENGTH_SHORT).show();
                constants.CurrentBus.setactive_sharing(true);
                constants.CurrentBus.setgoing_to_school(false);
            }
            tripToSchool.setEnabled(false);
            stopTrip.setEnabled(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void start_tripToSchool(View view) {
        try {

            if (!constants.CurrentBus.getactive_sharing()) {

                Map<String, Object> bus_update = new HashMap<>();
                bus_update.put("going_to_school", true);
                bus_update.put("active_sharing", true);
                fStore.collection("Bus").document(constants.CurrentBus.getbus_id()).update(bus_update);
                Toast.makeText(this, "Trip started from School", Toast.LENGTH_SHORT).show();
                constants.CurrentBus.setactive_sharing(true);
                constants.CurrentBus.setgoing_to_school(true);
            }
            tripFromSchool.setEnabled(false);
            stopTrip.setEnabled(true);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void start_stopTrip(View view) {
        try {

            if (constants.CurrentBus.getactive_sharing() || constants.CurrentBus.getactive_sharing()) {

                Map<String, Object> bus_update = new HashMap<>();
                bus_update.put("going_to_school", false);
                bus_update.put("active_sharing", false);
                fStore.collection("Bus").document(constants.CurrentBus.getbus_id()).update(bus_update);
                Toast.makeText(this, "Trip has been stopped", Toast.LENGTH_SHORT).show();
                constants.CurrentBus.setactive_sharing(false);
                constants.CurrentBus.setgoing_to_school(false);
            }
            tripFromSchool.setEnabled(true);
            tripToSchool.setEnabled(true);

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        BitmapDescriptor logo = BitmapDescriptorFactory.fromResource(R.drawable.logo1);
        // Add a marker in Sydney and move the camera
//        LatLng curr = new LatLng(Double.parseDouble(b.getcurrent_lat()), Double.parseDouble(b.getcurrent_long()));

        if (constants.CurrentBus != null) {


            LatLng curr = new LatLng(Double.parseDouble(constants.CurrentBus.getcurrent_lat()), Double.parseDouble(constants.CurrentBus.getcurrent_long()));
            LatLng des = new LatLng(Double.parseDouble(constants.CurrentBus.getdestination_lat()), Double.parseDouble(constants.CurrentBus.getdestination_long()));
            LatLng source = new LatLng(Double.parseDouble(constants.CurrentBus.getsource_lat()), Double.parseDouble(constants.CurrentBus.getsource_long()));

            if (!constants.CurrentBus.getgoing_to_school()) {

                des = new LatLng(Double.parseDouble(constants.CurrentBus.getsource_lat()), Double.parseDouble(constants.CurrentBus.getsource_long()));
                source = new LatLng(Double.parseDouble(constants.CurrentBus.getdestination_lat()), Double.parseDouble(constants.CurrentBus.getdestination_long()));
            }


            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(des).title(constants.CurrentBus.getdestination()));
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(source).title(constants.CurrentBus.getsource()));
            mMap.addMarker(new MarkerOptions().icon(logo).position(curr).title("Going to " + constants.CurrentBus.getdestination()));

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

            mMap.moveCamera(CameraUpdateFactory.newLatLng(des));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 25));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(25), 2000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curr)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }

    }


    public void getdata()
    {
        FirebaseFirestore.getInstance().collection("Bus").document(constants.CurrentUser.getbus_id().trim()).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();

                                        boolean as = (boolean) doc.getData().get("active_sharing");
                                        boolean gs = (boolean) doc.getData().get("going_to_school");
                                        constants.CurrentBus = new BusModel(
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
                                        );
                                    }
//                    showDriverLocationOnMap();

                            }
                        }
                );
    }



}