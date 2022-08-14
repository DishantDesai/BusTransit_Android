package com.example.school_bus_transit.parents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.Registration;
import com.example.school_bus_transit.driver.DriverProfile;
import com.example.school_bus_transit.helper.constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class parentProfile extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 22;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;

    TextView schools;
    boolean[] selectedSchools;
    TextInputLayout parentFullName,email,parentPhoneNo;
    EditText parentAddress;
    RadioGroup gender;
    ShapeableImageView parentProfileImage;
    Button save;
    String[] allSchoolName = new String[constants.allschool.size()];
    ArrayList<Integer> SelectedSchoolsList = new ArrayList<>();
     String photo_url = "",user_lat,user_long;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    ShapeableImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);
        getSupportActionBar();

        //Firebase Object Initialisation
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        selectedSchools = new boolean[allSchoolName.length];

        for(int i=0;i<constants.allschool.size();i++)
        {
            allSchoolName[i] = constants.allschool.get(i).getname();

            if(constants.CurrentUser.getschool_id().contains(constants.allschool.get(i).getname()))
            {
                SelectedSchoolsList.add(i);
                selectedSchools[i]=true;
            }
        }

        save = findViewById(R.id.save_parent_info);
        parentProfileImage = findViewById(R.id.parent_image);
        email = findViewById(R.id.parent_email);
        parentFullName = findViewById(R.id.parent_full_name);
        parentPhoneNo = findViewById(R.id.parent_phone_no);
        parentAddress = findViewById(R.id.parent_address);
        schools = findViewById(R.id.parent_schools);
        gender = (RadioGroup) findViewById(R.id.parent_gender_grp);

        email.getEditText().setText(constants.CurrentUser.getemail_id());
        parentFullName.getEditText().setText(constants.CurrentUser.getfullName());
        parentPhoneNo.getEditText().setText(constants.CurrentUser.getphone_no());
        parentAddress.setText(constants.CurrentUser.getaddress());
        user_lat = constants.CurrentUser.getuser_lat();
        user_long = constants.CurrentUser.getuser_long();
        photo_url = constants.CurrentUser.getphoto_url();
        
        

        //Select Profile Image
        parentProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        //Select multiple schools from dropdown

        schools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parentProfile.this);
                builder.setTitle("Select Schools");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(allSchoolName, selectedSchools, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if(b){
                            SelectedSchoolsList.add(i);
                            Collections.sort(SelectedSchoolsList);
                        }else{
                            SelectedSchoolsList.remove(Integer.valueOf(i));
                        }
                    }
                });
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder strBldr = new StringBuilder();
                        for (int j=0; j<SelectedSchoolsList.size();j++){
                            strBldr.append(allSchoolName[SelectedSchoolsList.get(j)]);
                            if(j != SelectedSchoolsList.size()-1){
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
                            SelectedSchoolsList.clear();
                            schools.setText("");
                        }
                    }
                });
                builder.show();
            }
        });


        //Select Address from autocomplete
        parentAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        parentProfileImage.setClipToOutline(true);

        //Initialise Places
        Places.initialize(getApplicationContext(),"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw");
        parentAddress.setFocusable(false);
        parentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldsList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsList)
                        .build(parentProfile.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    private Boolean isNameValid(){
        String name = parentFullName.getEditText().getText().toString();
        if(name.isEmpty()){
            parentFullName.setError("Field cannot be empty!");
            return false;
        }else{
            parentFullName.setError(null);
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
        String phoneVal = parentPhoneNo.getEditText().getText().toString();
        if(phoneVal.isEmpty()){
            parentPhoneNo.setError("Field cannot be empty!");
            return false;
        }else{
            parentPhoneNo.setError(null);
            return true;
        }
    }

    private Boolean isAddressValid(){
        String addressVal = parentAddress.getText().toString();
        if(addressVal.isEmpty()){
            parentAddress.setError("Field cannot be empty!");
            return false;
        }else{
            parentAddress.setError(null);
            return true;
        }
    }
    private Boolean isSchoolValid(){
        if(SelectedSchoolsList.isEmpty()){
            schools.setError("Field cannot be empty!");
            return false;
        }else{
            schools.setError(null);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();

            // Setting image on image view using Bitmap

            profileImage.setImageURI(filePath);
            uploadImage();
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                parentAddress.setText(place.getAddress());
                System.out.println("Lat: " + place.getLatLng().latitude + "Long: " +place.getLatLng().longitude);
                user_lat = String.valueOf(place.getLatLng().latitude);
                user_long = String.valueOf(place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(parentProfile.this,status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            UploadTask uploadTask = ref.putFile(filePath);
            uploadTask
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();

                                    Toast
                                            .makeText(parentProfile.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(parentProfile.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        photo_url = task.getResult().toString();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    public void saveUser(View view) {
        String emailVal = email.getEditText().getText().toString();
        String fullName = parentFullName.getEditText().getText().toString();
        String phoneNoVal = parentPhoneNo.getEditText().getText().toString();
        String addressVal = parentAddress.getText().toString();

        int genderIdVal = gender.getCheckedRadioButtonId();
        String genderVal = ((RadioButton) findViewById(genderIdVal)).getText().toString();

       
        try {
            if (!isSchoolValid() | !isEmailValid() | !isPhoneNoValid() | !isAddressValid()) {
                return;
            }
            String[] SelectedSchools = new String[SelectedSchoolsList.size()];
            for (int i = 0; i < SelectedSchoolsList.size(); i++) {
                SelectedSchools[i] = allSchoolName[SelectedSchoolsList.get(i)];
            }

            Map<String,Object> user = new HashMap<>();
            user.put("fullName",fullName);
            user.put("email_id",emailVal);
            user.put("phone_no",phoneNoVal);
            user.put("address",addressVal);
            user.put("user_type", "PARENT");
            user.put("user_id",constants.CurrentUser.getuser_id());
            user.put("bus_id","");
            user.put("gender",genderVal);
            user.put("photo_url",photo_url);
            user.put("user_lat",user_lat);
            user.put("user_long",user_long);

            user.put("school_id", Arrays.asList(SelectedSchools));


            fStore.collection("User").document(constants.CurrentUser.getuser_id()).update(user);

            Toast
                    .makeText(parentProfile.this,
                            "Profile Updated Successfully . ",
                            Toast.LENGTH_SHORT)
                    .show();
            
            
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
