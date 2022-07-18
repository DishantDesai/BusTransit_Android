package com.example.school_bus_transit.driver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.Registration;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DriverProfile extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;

    TextView schools;
    boolean[] selectedSchools;
    TextInputLayout driverFullName,email,driverPhoneNo;
    EditText driverAddress;
    RadioGroup userType,gender;
    ShapeableImageView driverProfileImage;
    Button save;
    String[] schoolArr = {"Concordia University","John Abbot","Vanier"};
    ArrayList<Integer> schoolsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        getSupportActionBar();

        //private Uri filePath;
        //private String photo_url = null,user_lat,user_long;

        save = findViewById(R.id.save_driver_info);
        driverProfileImage = findViewById(R.id.driver_image);
        email = findViewById(R.id.email);
        driverFullName = findViewById(R.id.driver_full_name);
        driverPhoneNo = findViewById(R.id.driver_phone_no);
        driverAddress = findViewById(R.id.driver_address);
        schools = findViewById(R.id.schools);
        userType = (RadioGroup) findViewById(R.id.user_type_grp);
        gender = (RadioGroup) findViewById(R.id.gender_grp);


        //Select Profile Image
        driverProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });


        //Select multiple schools from dropdown
        selectedSchools = new boolean[schoolArr.length];
        schools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DriverProfile.this);
                builder.setTitle("Select Schools");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(schoolArr, selectedSchools, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if(b){
                            schoolsList.add(i);
                            Collections.sort(schoolsList);
                        }else{
                            schoolsList.remove(Integer.valueOf(i));
                        }
                    }
                });
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder strBldr = new StringBuilder();
                        for (int j=0; j<schoolsList.size();j++){
                            strBldr.append(schoolArr[schoolsList.get(j)]);
                            if(j != schoolsList.size()-1){
                                strBldr.append(",");
                            }
                        }
                        schools.setText(strBldr.toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j=0;j< selectedSchools.length;j++){
                            selectedSchools[j] = false;
                            schoolsList.clear();
                            schools.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

        //User type changed listener
        userType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int userTypeIdVal = userType.getCheckedRadioButtonId();
                String userTypeVal = ((RadioButton) findViewById(userTypeIdVal)).getText().toString();

                if(userTypeVal.equalsIgnoreCase("driver")){
                    schools.setError(null);
                    schools.setEnabled(false);
                    schools.setAlpha((float) 0.8);

                    for(int j=0;j< selectedSchools.length;j++){
                        selectedSchools[j] = false;
                        schoolsList.clear();
                        schools.setText("");
                    }
                }else{

                    schools.setEnabled(true);
                    schools.setAlpha((float) 1);
                }
            }
        });

        //Select Address from autocomplete
        driverAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println("call ajsdjd");
            }
        });

        //press Save for data saving
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser(view);
            }
        });

        driverProfileImage.setClipToOutline(true);

        //Initialise Places
        Places.initialize(getApplicationContext(),"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw");
        driverAddress.setFocusable(false);
        driverAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldsList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsList)
                        .build(DriverProfile.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    private Boolean isNameValid(){
        String name = driverFullName.getEditText().getText().toString();
        if(name.isEmpty()){
            driverFullName.setError("Field cannot be empty!");
            return false;
        }else{
            driverFullName.setError(null);
            return true;
        }
    }

    Boolean isEmailValid(){
        String emailVal= email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(emailVal.isEmpty()){
            email.setError("Field cannot be empty!");
            return false;
        }else if(!emailVal.matches(emailPattern)){
            email.setError("Invalid email address");
            return false;
        }else{
            email.setError(null);
            return true;
        }
    }
    private Boolean isPhoneNoValid(){
        String phoneVal = driverPhoneNo.getEditText().getText().toString();
        if(phoneVal.isEmpty()){
            driverPhoneNo.setError("Field cannot be empty!");
            return false;
        }else{
            driverPhoneNo.setError(null);
            return true;
        }
    }

    private Boolean isAddressValid(){
        String addressVal = driverAddress.getText().toString();
        if(addressVal.isEmpty()){
            driverAddress.setError("Field cannot be empty!");
            return false;
        }else{
            driverAddress.setError(null);
            return true;
        }
    }
    private Boolean isSchoolValid(){
        if(schoolsList.isEmpty()){
            schools.setError("Field cannot be empty!");
            return false;
        }else{
            schools.setError(null);
            return true;
        }
    }

    public void saveUser(View view) {
        String emailVal = email.getEditText().toString();
        String fullName = driverFullName.getEditText().toString();
        String phoneNoVal = driverPhoneNo.getEditText().toString();
        String addressVal = driverAddress.getText().toString();


        try {
            if (!isSchoolValid() | !isEmailValid() | !isPhoneNoValid() | !isAddressValid()) {
                return;
            }
            String[] arr = new String[schoolsList.size()];
            for (int i = 0; i < schoolsList.size(); i++) {
                arr[i] = schoolArr[schoolsList.get(i)];
            }
        }
        catch(Exception e)
            {
                System.out.println(e);
            }
        }
    //Image Picker
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }


    }




