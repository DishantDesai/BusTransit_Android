package com.example.school_bus_transit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.SchoolModel;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

public class Registration extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    TextView signin;
    TextView schools;
    boolean[] selectedSchools;
    TextInputLayout fullName,email,phoneNo,password,confirmPassword;
    EditText address;
    RadioGroup userType,gender;
    ShapeableImageView profileImage;
    Button regBtn;
    String[] schoolArr = {};
    ArrayList<Integer> schoolsList = new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    private String photo_url = null,user_lat,user_long;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        signin = findViewById(R.id.signin);
        fullName = findViewById(R. id.full_name);
        email = findViewById(R.id.email);
        password= findViewById(R.id.password);
        confirmPassword= findViewById(R.id.confirm_pass);
        phoneNo = findViewById(R.id.phone_no);
        address = findViewById(R.id.address);
        userType = (RadioGroup) findViewById(R.id.user_type_grp);
        gender = (RadioGroup) findViewById(R.id.gender_grp);
        regBtn = findViewById(R.id.register);
        profileImage = findViewById(R.id.profile_image);
        //Firebase Object Initialisation
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String mString = "Sign Up";
        SpannableString spannableStr = new SpannableString("Sign In");
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableStr.setSpan(underlineSpan, 0, mString.length(), 0);
        signin.setText(spannableStr);
        schools = findViewById(R.id.schools);

        //Get school list for school dropdown
        getSchoolList();
        schoolArr = new String[constants.allschool.size()];
        for(int i=0;i<constants.allschool.size();i++){
            schoolArr[i] = constants.allschool.get(i).getname();
        }
        //Select Profile Image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        //Redirect To SignIn Screen
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, Login.class));
            }
        });

        //Select multiple schools from dropdown
        selectedSchools = new boolean[schoolArr.length];

        schools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
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
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println("call ajsdjd");
            }
        });

        //Submit button for registration
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(view);
            }
        });

        profileImage.setClipToOutline(true);

        //Initialise Places
        Places.initialize(getApplicationContext(),"AIzaSyAgpLONoQLPhvXWh05qs8cCBdmZS9NDolw");
        address.setFocusable(false);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldsList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsList)
                        .build(Registration.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    private Boolean isNameValid(){
        String name = fullName.getEditText().getText().toString();
        if(name.isEmpty()){
            fullName.setError("Field cannot be empty!");
            return false;
        }else{
            fullName.setError(null);
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
        String phoneVal = phoneNo.getEditText().getText().toString();
        String phoneValidation = "^" +
                "\\" +
                "d{3}-" +
                "\\" +
                "d{3}-" +
                "\\d{4}$";
        if(phoneVal.isEmpty())
        {
            phoneNo.setError("Field cannot be empty!");
            return false;
        }else if(!phoneVal.matches(phoneValidation))
        {
            phoneNo.setError("PhoneNo should be in 345-345-3456 formate.");
            return false;
        }
        else
        {
            phoneNo.setError(null);
            return true;
        }
    }
    private Boolean isPasswordValid(){
        String passwordVal = password.getEditText().getText().toString();
        String passwordPattern = "^" +
                "(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{4,}" +
                "$";
        if(passwordVal.isEmpty()){
            password.setError("Field cannot be empty!");
            return false;
        }else if(!passwordVal.matches(passwordPattern))
        {
            password.setError("Password should contains capital letters(A-Z),small letter(a-z),numbers(0-9),one special characters.");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }
    private Boolean isConfirmPasswordValid(){
        String passwordVal = password.getEditText().getText().toString();
        String confPasswordVal = confirmPassword.getEditText().getText().toString();
        if(confPasswordVal.isEmpty()){
            confirmPassword.setError("Field cannot be empty!");
            return false;
        }else if(!confPasswordVal.equals(passwordVal)){
            confirmPassword.setError("Confirm password must match password");
            return false;
        }else{
            confirmPassword.setError(null);
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
    private Boolean isSchoolValid(){
        if(schoolsList.isEmpty()){
            schools.setError("Field cannot be empty!");
            return false;
        }else{
            schools.setError(null);
            return true;
        }
    }

    //Add data to firebase
    public void registerUser(View view){
        String fullnameVal = fullName.getEditText().getText().toString();
        String emailVal= email.getEditText().getText().toString();
        String passwordVal = password.getEditText().getText().toString();
        String addressVal = address.getText().toString();
        String phoneVal = phoneNo.getEditText().getText().toString();
        int userTypeIdVal = userType.getCheckedRadioButtonId();
        String userTypeVal = ((RadioButton) findViewById(userTypeIdVal)).getText().toString();
        int genderIdVal = gender.getCheckedRadioButtonId();
        String genderVal = ((RadioButton) findViewById(genderIdVal)).getText().toString();

        try{
            if(!isNameValid() | !isEmailValid() | !isPhoneNoValid() | !isPasswordValid() | !isConfirmPasswordValid() | !isAddressValid() | (userTypeVal.equalsIgnoreCase("parent") && !isSchoolValid())){
                return;
            }
            String[] arr = new String[schoolsList.size()];
            for (int i =0;i<schoolsList.size();i++){
                arr[i] = schoolArr[schoolsList.get(i)];
            }

            mAuth.createUserWithEmailAndPassword(emailVal,passwordVal).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("User").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fullName",fullnameVal);
                        user.put("email_id",emailVal);
                        user.put("phone_no",phoneVal);
                        user.put("address",addressVal);
                        user.put("user_type", userTypeVal);
                        user.put("user_id",userID);
                        user.put("bus_id","");
                        user.put("gender",genderVal);
                        user.put("photo_url",photo_url);
                        user.put("user_lat",user_lat);
                        user.put("user_long",user_long);

                        user.put("school_id", Arrays.asList(arr));

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        Toast.makeText(Registration.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registration.this, Login.class));
                    }else{
                        Toast.makeText(Registration.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
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
                address.setText(place.getAddress());
                System.out.println("Lat: " + place.getLatLng().latitude + "Long: " +place.getLatLng().longitude);
                user_lat = String.valueOf(place.getLatLng().latitude);
                user_long = String.valueOf(place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(Registration.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getSchoolList() {
        FirebaseFirestore.getInstance().collection("School")
                .get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<SchoolModel> school = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        school.add(new SchoolModel(doc.getData().get("name").toString(),
                                                        doc.getData().get("phone_no").toString(),
                                                        doc.getData().get("school_id").toString(),
                                                        doc.getData().get("email_id").toString(),
                                                        doc.getData().get("address").toString(),
                                                        doc.getData().get("lat").toString(),
                                                        doc.getData().get("long").toString()
                                                )

                                        );
                                    }
                                    constants.allschool = school;
                                }
                            }
                        }
                );
    }
    // UploadImage method
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
                                            .makeText(Registration.this,
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
                                    .makeText(Registration.this,
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

}