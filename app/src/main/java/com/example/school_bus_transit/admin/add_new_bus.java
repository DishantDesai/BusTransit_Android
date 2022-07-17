package com.example.school_bus_transit.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.school_bus_transit.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class add_new_bus extends AppCompatActivity {
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText endDestination;
    TextInputLayout busNo;
    Button addBus;

    private String photo_url = null,user_lat,user_long;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bus);
        getSupportActionBar();

        busNo = findViewById(R.id.bus_no);
        endDestination = findViewById(R.id.end_address);
        addBus = findViewById(R.id.add_bus);


        //Select End Destination from autocomplete
        endDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println("call ajsdjd");
            }
        });

        addBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBus(view);

            }
        });
        //Initialise Places
        Places.initialize(getApplicationContext(),"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw");
        endDestination.setFocusable(false);
        endDestination.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<Place.Field> fieldsList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsList)
                        .build(add_new_bus.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                endDestination.setText(place.getAddress());
                System.out.println("Lat: " + place.getLatLng().latitude + "Long: " +place.getLatLng().longitude);
                user_lat = String.valueOf(place.getLatLng().latitude);
                user_long = String.valueOf(place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(add_new_bus.this,status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



        private Boolean isBusNumberValid()
        {
            String name = busNo.getEditText().getText().toString();
            if(name.isEmpty()){
                busNo.setError("Field cannot be empty!");
                return false;
            }else
            {
                busNo.setError(null);
                return true;
            }
        }

        private Boolean isAddressValid()
        {
            String addressVal = endDestination.getText().toString();
            if(addressVal.isEmpty()){
                endDestination.setError("Field cannot be empty!");
                return false;
            }else{
                endDestination.setError(null);
                return true;
            }
        }

        public void addBus(View view){
        String addBusNo = busNo.getEditText().toString();
        String endDestinationAdd = endDestination.getText().toString();

        try {
            if (!isBusNumberValid() | ! isAddressValid())
            {
                return;
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        }

    }
