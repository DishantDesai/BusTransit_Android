package com.example.school_bus_transit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.school_bus_transit.admin.adminDashBoard;
import com.example.school_bus_transit.driver.DriverNotification;
import com.example.school_bus_transit.driver.DriverProfile;
import com.example.school_bus_transit.driver.driver_not_allowed_screen;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Login extends AppCompatActivity {
    private TextInputLayout email,pass;
    private Button loginBtn;
    TextView signup;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        loginBtn=findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        signup = findViewById(R.id.signup);
        String mString = "Sign Up";
        SpannableString spannableStr = new SpannableString("Sign Up");
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableStr.setSpan(underlineSpan,0, mString.length(), 0);
        signup.setText(spannableStr);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(Login.this, Registration.class));
                startActivity(new Intent(Login.this, Registration.class));
            }
        });

        //Email-pass signIn
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(view);
            }
        });
    }
    private void loginUser(View view){

        String emailAddress= email.getEditText().getText().toString().trim();
        String password = pass.getEditText().getText().toString().trim();
        System.out.println("email" + emailAddress);
        System.out.println("password" + password);
        pass.setError(null);
        email.setError(null);
        if(password.isEmpty() || emailAddress.isEmpty()) {

            if (TextUtils.isEmpty(password)) {
                pass.setError("Please enter Password");
                pass.requestFocus();
            }
            if (TextUtils.isEmpty(emailAddress)) {
                email.setError("Please enter Email");
                email.requestFocus();
            }
        }else {

            if(password.equals("admin") || emailAddress.equals("admin"))
            {
                startActivity(new Intent(Login.this, adminDashBoard.class));
                Toast.makeText(Login.this, "User logged in successfully !!! " , Toast.LENGTH_SHORT).show();
            }
            else
            {
                mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userRedirect();
                        } else {
                            Toast.makeText(Login.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }
    //Redirect user to appropriate screen based on User Type(Chef,Foodie)
    private void userRedirect(){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        FirebaseFirestore.getInstance().collection("User").whereEqualTo("user_id",user.getUid())
                .get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                List<String> schoolList = ((List<String>) doc.get("school_id"));

                                constants.CurrentUser  =  new UserModel(
                                        doc.get("user_id").toString(),
                                        doc.get("photo_url") != null ? doc.get("photo_url").toString() :"",
                                        doc.get("gender").toString(),
                                        doc.get("bus_id") !=null ? doc.get("bus_id").toString() :"",
                                        doc.get("fullName").toString(),
                                        doc.get("phone_no").toString(),
                                        schoolList,
                                        doc.get("email_id").toString(),
                                        doc.get("address").toString(),
                                        doc.get("user_lat").toString(),
                                        doc.get("user_long").toString(),
                                        doc.get("user_type").toString()

                                );

                            }
                            if(constants.CurrentUser.getUserType().equalsIgnoreCase(constants.DRIVER))
                            {
                                if(constants.CurrentUser.getbus_id().equals(""))
                                {
                                    startActivity(new Intent(Login.this, driver_not_allowed_screen.class));
                                }
                                else
                                {
                                    startActivity(new Intent(Login.this, HomeScreen.class));

                                }
                            }
                            else
                            {
                                startActivity(new Intent(Login.this, HomeScreen.class));
                            }
                            Toast.makeText(Login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
        );
    }
}

/*
*
*
* GTFS Realtime Overview
Note: To see a detailed list of recommended practices for feeds about realtime public transportation information, refer to the GTFS Realtime Best Practices on the Mobility Data site.
Providing users transit data updates in real time greatly enhances their experience of your transit services. Providing up-to-date information about current arrival and departure times allows users to smoothly plan their trips. As a result, in case of an unfortunate delay, a rider would be relieved to know that they can stay home a little bit longer.

GTFS Realtime is a feed specification that allows public transportation agencies to provide realtime updates about their fleet to application developers. It is an extension to GTFS (General Transit Feed Specification), an open data format for public transportation schedules and associated geographic information. GTFS Realtime was designed around ease of implementation, good GTFS interoperability and a focus on passenger information.

The specification was designed through a partnership of the initial Live Transit Updates partner agencies, a number of transit developers and Google. The specification is published under the Apache 2.0 License.

What is Live Transit Updates for Google Maps?
Live Transit Updates is a service providing real-time transit updates to users of Google Maps and Google Maps for mobile. These updates include live departure and arrival times to transit stations and service alerts.

Live Transit Updates provide two types of real-time updates to users: live departure times and service alerts. Transit partners provide these updates in their feeds. We created the GTFS realtime feed format to complement the widely-used GTFS format for static transit schedules. Your feed needs to be available on a location where we can fetch it periodically. The system immediately processes feed updates as Live Transit Updates information.

How do I start?
Continue reading the overview below.
Decide which feed entities you will be providing.
Take a look at example feeds.
Create your own feeds using the reference.
Publish your feed.
Overview of GTFS Realtime feeds
The specification currently supports the following types of information:

Trip updates - delays, cancellations, changed routes
Service alerts - stop moved, unforeseen events affecting a station, route or the entire network
Vehicle positions - information about the vehicles including location and congestion level
A feed may, although not required to, combine entities of different types. Feeds are served via HTTP and updated frequently. The file itself is a regular binary file, so any type of webserver can host and serve the file (other transfer protocols might be used as well). Alternatively, web application servers could also be used which as a response to a valid HTTP GET request will return the feed. There are no constraints on how frequently nor on the exact method of how the feed should be updated or retrieved.

Because GTFS Realtime allows you to present the actual status of your fleet, the feed needs to be updated regularly - preferably whenever new data comes in from your Automatic Vehicle Location system.

More about feed entities...

Data format
The GTFS Realtime data exchange format is based on Protocol Buffers

Protocol buffers are a language- and platform-neutral mechanism for serializing structured data (think XML, but smaller, faster, and simpler). The data structure is defined in a gtfs-realtime.proto file, which then is used to generate source code to easily read and write your structured data from and to a variety of data streams, using a variety of languages – e.g. Java, C++ or Python.

More about Protocol Buffers....

Data structure
The hierarchy of elements and their type definitions are specified in the gtfs-realtime.proto file.

This text file is used to generate the necessary libraries in your choice of programming language. These libraries provide the classes and functions needed for generating valid GTFS Realtime feeds. The libraries not only make feed creation easier but also ensure that only valid feeds are produced.

More about the data structure...

Getting Help
To participate in discussions around GTFS Realtime and suggest changes and additions to the specification, join the GTFS Realtime mailing list.

Google Maps and Live Transit Updates
Live Transit Updates is a feature within Google Maps that provides users with realtime transit information. If you are working for a public transportation agency that is interested in providing realtime updates to Google Maps, please visit the Google Transit Partner Page.
*
* */