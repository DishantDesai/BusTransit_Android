package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.school_bus_transit.Login;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.Registration;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_new_school extends AppCompatActivity {
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText address;
    TextInputLayout schoolName, email, phoneNo;
    Button addSchool;

    private String photo_url = null,school_lat,school_long;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    String doc_id = "";

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

        //Firebase Object Initialisation
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


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
                    school_lat = String.valueOf(place.getLatLng().latitude);
                    school_long = String.valueOf(place.getLatLng().longitude);
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

    public void addSchool(View view)
    {
        String name = schoolName.getEditText().getText().toString();
        String email_id = email.getEditText().getText().toString();
        String addressVal = address.getText().toString();
        String phone_no = phoneNo.getEditText().getText().toString();

        try {
            if (!isSchoolNameValid() | !isEmailValid() | !isPhoneNoValid() | !isAddressValid()) {
                return;
            }

            DocumentReference documentReference = fStore.collection("School").document();
            Map<String,Object> school = new HashMap<>();
            school.put("name",name);
            school.put("phone_no",phone_no);
            school.put("phone_no",phone_no);
            school.put("school_id","");
            school.put("email_id", email_id);
            school.put("address",addressVal);
            school.put("lat",school_lat);
            school.put("long",school_long);


            documentReference.set(school).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {

                    doc_id = documentReference.getId().toString();

                    Map<String,Object> school_update = new HashMap<>();
                    school_update.put("school_id",doc_id);
                    fStore.collection("School").document(doc_id).update(school_update);
                    Toast.makeText(add_new_school.this, "School added Successfully", Toast.LENGTH_SHORT).show();
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
}
