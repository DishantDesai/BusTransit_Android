package com.example.school_bus_transit.parents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.admin.DriverBusInfo;
import com.example.school_bus_transit.helper.DirectionsJSONParser;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.helper.fetchRoute;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class busTrack extends AppCompatActivity implements OnMapReadyCallback {

    UserModel d;
    BusModel b;
    SchoolModel s;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Button rDriver;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    Button getDriverInfo;
    TextView distance , duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_track);

        getdata();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.busTrack_map);
        mapFragment.getMapAsync(this);

        distance = (TextView) findViewById(R.id.distance);
        duration = (TextView) findViewById(R.id.duration);

        getDriverInfo = findViewById(R.id.get_driver_info);
        getDriverInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(busTrack.this, parentDriverProfile.class));
            }
        });

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Your code to run in GUI thread here
//                        onMapReady(mMap);
//                    }
//                });
//
//            }
//        }, 12000);
    }

    String getMapsApiDirectionsUrl()
    {
        String str_dest = "destination=" + constants.CurrentBus.getdestination_lat() + "," + constants.CurrentBus.getdestination_long();
        if(constants.CurrentBus.getgoing_to_school())
        {
            str_dest = "destination=" + constants.CurrentBus.getsource_lat() + "," + constants.CurrentBus.getsource_long();
        }
        String str_origin = "origin=" + constants.CurrentBus.getcurrent_lat()+ "," + constants.CurrentBus.getcurrent_long();


        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + "driving" + "&alternatives=true"
                +"&key="+"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    String getMapsApiTimeUrl() {
        String str_origin = "origins=" + constants.CurrentBus.getcurrent_lat()+ "," + constants.CurrentBus.getcurrent_long();
        String str_dest = "destinations=" + constants.CurrentBus.getdestination_lat() + "," + constants.CurrentBus.getdestination_long();
        String parameters = "units=imperial&"+str_origin + "&" + str_dest
                +"&key="+"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters;
        return url;
    }

    void getTime()
    {
        String myUrl = getMapsApiTimeUrl();
        StringRequest myRequest = new StringRequest(Request.Method.GET, myUrl,
                response -> {
                    try{
                        //Create a JSON object containing information from the API.
                        JSONObject myJsonObject = new JSONObject(response);
                        System.out.println(myJsonObject);

                        String dis  = myJsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").get("text").toString();
                        String dur  = myJsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").get("text").toString();

                        duration.setText(dur);
                        distance.setText(dis);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> Toast.makeText(busTrack.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(myRequest);
    }

    public void getRouteUpdate()
    {

        StringRequest myRequest = new StringRequest(Request.Method.GET, getMapsApiDirectionsUrl(),
                response -> {
                    try{
//                        //Create a JSON object containing information from the API.
//                        JSONObject myJsonObject = new JSONObject(response);
//                        System.out.println(myJsonObject);

                        constants.routes = new DirectionsJSONParser().parse(new JSONObject(response));
                        onMapReady(mMap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> Toast.makeText(busTrack.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(myRequest);


    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        BitmapDescriptor logo = BitmapDescriptorFactory.fromResource(R.drawable.logo1);
        BitmapDescriptor logo_home = BitmapDescriptorFactory.fromResource(R.drawable.home_icon);
        BitmapDescriptor logo_school = BitmapDescriptorFactory.fromResource(R.drawable.school_icon);




        if (!constants.CurrentBus.getcurrent_lat().equals("") && constants.CurrentBus.getactive_sharing()) {

            LatLng curr = new LatLng(Double.parseDouble(constants.CurrentBus.getcurrent_lat()), Double.parseDouble(constants.CurrentBus.getcurrent_long()));
            LatLng des = new LatLng(Double.parseDouble(constants.CurrentBus.getdestination_lat()), Double.parseDouble(constants.CurrentBus.getdestination_long()));
            LatLng source = new LatLng(Double.parseDouble(constants.CurrentBus.getsource_lat()), Double.parseDouble(constants.CurrentBus.getsource_long()));

            mMap.addMarker(new MarkerOptions().icon(logo_home).position(des).title(constants.CurrentBus.getdestination()));
            mMap.addMarker(new MarkerOptions().icon(logo_school).position(source).title(constants.CurrentBus.getsource()));
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

    private void getdata() {

        String bus_id = (String) getIntent().getSerializableExtra("bus_id");


        FirebaseFirestore.getInstance().collection("Bus").whereEqualTo("bus_id",bus_id).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for(DocumentSnapshot doc : value.getDocuments())
                        {
                            boolean as = (boolean) doc.getData().get("active_sharing");
                            boolean gs = (boolean) doc.getData().get("going_to_school");

                            constants.CurrentBus  =new BusModel(
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
                        getTime();
//                        fetchRoute myAsyncTasks = new fetchRoute();
//                        myAsyncTasks.execute(getMapsApiDirectionsUrl(),"","");
                        getRouteUpdate();  // --newly

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // Your code to run in GUI thread here
//                                onMapReady(mMap);
//                            }
//                        });


                    }
                });



    }

}