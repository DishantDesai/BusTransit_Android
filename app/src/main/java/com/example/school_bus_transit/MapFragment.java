package com.example.school_bus_transit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.school_bus_transit.helper.FirebaseHelper;
import com.example.school_bus_transit.helper.GPSTracker;
import com.example.school_bus_transit.helper.LocationGetter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.helper.fetchRoute;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{


    FirebaseFirestore fStore;
    Context context;

    private GoogleMap mMap;
    GPSTracker locationHelper;

    public SupportMapFragment supportMapFragment;
    Button tripFromSchool, tripToSchool, stopTrip;

    boolean isTripOn;
    boolean isFirstCall;

    int globleTimer = 2000;


    public MapFragment(Button tripFromSchool,Button tripToSchool,Button stopTrip, Context context) {
        // Required empty public constructor
        this.tripFromSchool = tripFromSchool;
        this.tripToSchool = tripToSchool;
        this.stopTrip = stopTrip;
        this.context = context;
        isTripOn = false;
        isFirstCall = true;

        fStore = FirebaseFirestore.getInstance();

        locationHelper  = new GPSTracker(context, new LocationGetter() {
            @Override
            public void LocationChangeListner(double latitude, double longitude) {

                if(constants.CurrentBus!=null && constants.CurrentBus.getactive_sharing())
                {
                    if(isTripOn)
                    {
                        LocationChange(latitude,longitude);

                         createRoute();
                         onMapReady(mMap);

                    }

                }
            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripFromSchool.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  start_tripFromSchool();
                                              }
                                          });

        tripToSchool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_tripToSchool();
            }
        });

        stopTrip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_stopTrip();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_map, container, false);
        supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(this);
        // Return view
        return view;
    }

    public void addMarker(GoogleMap googleMap, String title,LatLng latLong ,BitmapDescriptor logo,boolean isCameraFocusPoint)
    {

        googleMap.addMarker(new MarkerOptions()
                .position(latLong)
                .title(title)
                .icon(logo));

        if (isCameraFocusPoint)
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 10));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        clearMap(googleMap);


//        setGoogleMapType(googleMap);
        setZoom_and_Rotation(googleMap);

        if (constants.CurrentBus != null)
        {

            BitmapDescriptor logo_bus = BitmapDescriptorFactory.fromResource(R.drawable.logo1);
            BitmapDescriptor logo_home = BitmapDescriptorFactory.fromResource(R.drawable.home_icon);
            BitmapDescriptor logo_school = BitmapDescriptorFactory.fromResource(R.drawable.school_icon);

            LatLng curr = new LatLng(Double.parseDouble(constants.CurrentBus.getcurrent_lat()), Double.parseDouble(constants.CurrentBus.getcurrent_long()));
            LatLng des = new LatLng(Double.parseDouble(constants.CurrentBus.getdestination_lat()), Double.parseDouble(constants.CurrentBus.getdestination_long()));
            LatLng source = new LatLng(Double.parseDouble(constants.CurrentBus.getsource_lat()), Double.parseDouble(constants.CurrentBus.getsource_long()));



            if(constants.CurrentBus.getactive_sharing())
            {
                addMarker(googleMap,"Destination",des,logo_home,false);
                addMarker(googleMap,"School",source,logo_school,false);
                addMarker(googleMap,"Current",curr,logo_bus,true);

                //find all latlong of shortesh path and add into list and uncomment below lines ,  path will be ready in map
                createRoute();
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
            else
            {
                addMarker(googleMap,"Destination",des,logo_home,false);
                addMarker(googleMap,"School",source,logo_school,false);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(des));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 15));
            if(isFirstCall)
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(des));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(curr)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                isFirstCall = false;
            }

            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }




    }

    String getMapsApiDirectionsUrl() {

        String dest_lat = constants.CurrentBus.getgoing_to_school() ? constants.CurrentBus.getsource_lat() : constants.CurrentBus.getdestination_lat();
        String dest_long = constants.CurrentBus.getgoing_to_school() ? constants.CurrentBus.getsource_long() : constants.CurrentBus.getdestination_long();
        String str_origin = "origin=" + constants.CurrentBus.getcurrent_lat()+ "," + constants.CurrentBus.getcurrent_long();
        String str_dest = "destination=" + dest_lat + "," + dest_long;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + "driving" + "&alternatives=true"
                +"&key="+"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Toast.makeText(context,"new URL generated",Toast.LENGTH_SHORT);
        return url;
    }

    public void start_tripFromSchool() {
            System.out.println("Trip from school clicked   -1-");
        try {

            if (!constants.CurrentBus.getactive_sharing()) {

                Map<String, Object> bus_update = new HashMap<>();
                bus_update.put("going_to_school", false);
                bus_update.put("active_sharing", true);
                fStore.collection("Bus").document(constants.CurrentBus.getbus_id()).update(bus_update);
                Toast.makeText(context, "Trip started from School", Toast.LENGTH_SHORT).show();
                constants.CurrentBus.setactive_sharing(true);
                constants.CurrentBus.setgoing_to_school(false);

                isTripOn = true;
                createRoute();
                onMapReady(mMap);

            }
            tripToSchool.setEnabled(false);
            stopTrip.setEnabled(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void start_tripToSchool() {
        System.out.println("Trip to school clicked...........  -2-");
        try {

            if (!constants.CurrentBus.getactive_sharing()) {

                Map<String, Object> bus_update = new HashMap<>();
                bus_update.put("going_to_school", true);
                bus_update.put("active_sharing", true);
                fStore.collection("Bus").document(constants.CurrentBus.getbus_id()).update(bus_update);
                Toast.makeText(context, "Trip started to School", Toast.LENGTH_SHORT).show();
                constants.CurrentBus.setactive_sharing(true);
                constants.CurrentBus.setgoing_to_school(true);
                isTripOn = true;
                createRoute();
                onMapReady(mMap);

            }
            tripFromSchool.setEnabled(false);
            stopTrip.setEnabled(true);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void start_stopTrip() {
        System.out.println("Trip stoped clicked...........  -3-");
        try {

            if (constants.CurrentBus.getactive_sharing() || constants.CurrentBus.getactive_sharing()) {

                Map<String, Object> bus_update = new HashMap<>();
                bus_update.put("going_to_school", false);
                bus_update.put("active_sharing", false);
                fStore.collection("Bus").document(constants.CurrentBus.getbus_id()).update(bus_update);
                Toast.makeText(context, "Trip has been stopped", Toast.LENGTH_SHORT).show();
                constants.CurrentBus.setactive_sharing(false);
                constants.CurrentBus.setgoing_to_school(false);
                showOnlySource_and_Destination(mMap);
                isTripOn = false;
            }
            tripFromSchool.setEnabled(true);
            tripToSchool.setEnabled(true);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void LocationChange(double latitude, double longitude)
    {

        constants.CurrentBus.setcurrent_lat(String.valueOf(latitude));
        constants.CurrentBus.setcurrent_long(String.valueOf(longitude));

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // Your code to run in GUI thread here
//                onMapReady(mMap);
//            }
//        });

        FirebaseHelper.update2Field("Bus", constants.CurrentBus.getbus_id(),
                "current_lat", String.valueOf(latitude),
                "current_long", String.valueOf(longitude));
                Toast.makeText(context, latitude + " -  VVV - " + longitude, Toast.LENGTH_SHORT).show();
    }

    public void createRoute() {

        fetchRoute myAsyncTasks = new fetchRoute();
            myAsyncTasks.execute(getMapsApiDirectionsUrl(),"","");



    }


    public void showOnlySource_and_Destination(GoogleMap googleMap)
    {
        googleMap.clear();

        BitmapDescriptor logo_home = BitmapDescriptorFactory.fromResource(R.drawable.home_icon);
        BitmapDescriptor logo_school = BitmapDescriptorFactory.fromResource(R.drawable.school_icon);

        LatLng des = new LatLng(Double.parseDouble(constants.CurrentBus.getdestination_lat()), Double.parseDouble(constants.CurrentBus.getdestination_long()));
        LatLng source = new LatLng(Double.parseDouble(constants.CurrentBus.getsource_lat()), Double.parseDouble(constants.CurrentBus.getsource_long()));

        addMarker(googleMap,"Destination",des,logo_home,false);
        addMarker(googleMap,"School",source,logo_school,true);

    }

    // map configuration
    public  void  setGoogleMapType(GoogleMap googleMap)
    {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void setZoom_and_Rotation(GoogleMap googleMap)
    {
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    public void clearMap(GoogleMap googleMap)
    {
        googleMap.clear();
    }

}