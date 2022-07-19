package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.helper.constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_new_bus extends AppCompatActivity {
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText endDestination;
    EditText add_bus_no;
    Button addBus;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    String doc_id = "";
    String school_id = "",school_address="",school_lat="",school_long="";

    private String photo_url = null,user_lat,user_long;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bus);
        getSupportActionBar();


        add_bus_no = findViewById(R.id.add_bus_no);
        add_bus_no.setFocusable(false);
        int val = 100 + constants.allbus.size();
        add_bus_no.setText(String.valueOf(val));


        endDestination = findViewById(R.id.end_address);
        addBus = findViewById(R.id.add_bus);
        school_id = (String) getIntent().getSerializableExtra("school_id");

        school_address=(String) getIntent().getSerializableExtra("school_address");
        school_lat=(String) getIntent().getSerializableExtra("school_lat");
        school_long=(String) getIntent().getSerializableExtra("school_long");

        //Firebase Object Initialisation
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();




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
            String name = add_bus_no.getText().toString();
            if(name.isEmpty()){
                add_bus_no.setError("Field cannot be empty!");
                return false;
            }else
            {
                add_bus_no.setError(null);
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
        int addBusNo = Integer.parseInt(add_bus_no.getText().toString());
        String endDestinationAdd = endDestination.getText().toString();

        try {
            if (!isBusNumberValid() | ! isAddressValid())
            {
                return;
            }

            try {

                DocumentReference documentReference = fStore.collection("Bus").document();
                Map<String,Object> bus = new HashMap<>();
                bus.put("active_sharing",false);
                bus.put("bus_id","");
                bus.put("bus_number",addBusNo);
                bus.put("current_lat", school_lat);
                bus.put("current_long",school_long);
                bus.put("destination",endDestinationAdd);
                bus.put("destination_lat",user_lat);
                bus.put("destination_long", user_long);
                bus.put("going_to_school",false);
                bus.put("school_id",school_id);
                bus.put("source",school_address);
                bus.put("source_lat",school_lat);
                bus.put("source_long", school_long);


                documentReference.set(bus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {

                        doc_id = documentReference.getId().toString();

                        Map<String,Object> bus_update = new HashMap<>();
                        bus_update.put("bus_id",doc_id);
                        fStore.collection("Bus").document(doc_id).update(bus_update);
                        Toast.makeText(add_new_bus.this, "Bus added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                    }
                });

            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        }

    }
