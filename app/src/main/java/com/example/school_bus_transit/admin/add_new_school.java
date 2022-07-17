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

public class add_new_school extends AppCompatActivity {
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText address;
    TextInputLayout schoolName, email, phoneNo;
    Button addSchool;

    private String photo_url = null,user_lat,user_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_school);
        getSupportActionBar();

        schoolName = findViewById(R.id.schoolName);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phoneNo = findViewById(R.id.phone_no);
        addSchool = findViewById(R.id.add_school);


        //Select Address from autocomplete
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println("call ajsdjd");
            }
        });

        addSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSchool(view);
            }
        });

        //Initialise Places
        Places.initialize(getApplicationContext(),"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw");
        address.setFocusable(false);
        address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<Place.Field> fieldsList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsList)
                        .build(add_new_school.this);
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
                    address.setText(place.getAddress());
                    System.out.println("Lat: " + place.getLatLng().latitude + "Long: " +place.getLatLng().longitude);
                    user_lat = String.valueOf(place.getLatLng().latitude);
                    user_long = String.valueOf(place.getLatLng().longitude);
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Toast.makeText(add_new_school.this,status.getStatusMessage(),Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                return;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }


        private Boolean isSchoolNameValid()
    {
        String name = schoolName.getEditText().getText().toString();
        if(name.isEmpty()){
            schoolName.setError("Field cannot be empty!");
            return false;
        }else
        {
            schoolName.setError(null);
            return true;
        }
    }

    private boolean isEmailValid() {

        String emailVal = email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (emailVal.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!emailVal.matches(emailPattern)) {
            email.setError("Invalid email address");
            return false;
        }else{
            email.setError(null);
            return true;
        }

    }
    private Boolean isPhoneNoValid(){
        String phoneVal = phoneNo.getEditText().getText().toString();
        if(phoneVal.isEmpty()){
            phoneNo.setError("Field cannot be empty!");
            return false;
        }else{
            phoneNo.setError(null);
            return true;
        }
    }
    private Boolean isAddressValid(){
        String addressVal = address.getText().toString();
        if(addressVal.isEmpty()){
            address.setError("Field cannot be empty!");
            return false;
        }else{
            address.setError(null);
            return true;
        }
    }

    public void addSchool(View view){
        String addschoolName = schoolName.getEditText().toString();
        String emailVal = email.getEditText().toString();
        String addressVal = address.getText().toString();
        String phoneNoVal = phoneNo.getEditText().toString();

        try {
            if (!isSchoolNameValid() | !isEmailValid() | !isPhoneNoValid() | !isAddressValid()) {
                return;
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
