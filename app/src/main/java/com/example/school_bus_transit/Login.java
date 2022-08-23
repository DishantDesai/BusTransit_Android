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
import com.example.school_bus_transit.driver.DriverHomeScreen;
import com.example.school_bus_transit.driver.driver_not_allowed_screen;
import com.example.school_bus_transit.helper.FirebaseHelper;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.UserModel;
import com.example.school_bus_transit.parents.ParentHomeScreen;
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

        email.getEditText().setText("admin");
        pass.getEditText().setText("admin");

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
                        if (task.isSuccessful())
                        {
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
                                        doc.get("user_id").toString().trim(),
                                        doc.get("photo_url") != null ? doc.get("photo_url").toString() :"",
                                        doc.get("gender").toString(),
                                        doc.get("bus_id") !=null ? doc.get("bus_id").toString().trim() :"",
                                        doc.get("fullName").toString(),
                                        doc.get("phone_no").toString(),
                                        schoolList,
                                        doc.get("email_id").toString(),
                                        doc.get("address").toString(),
                                        doc.get("user_lat").toString().trim(),
                                        doc.get("user_long").toString().trim(),
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
                                    FirebaseHelper.getBusModel();
                                    startActivity(new Intent(Login.this, DriverHomeScreen.class));
//                                    startActivity(new Intent(Login.this, DriverNotification.class));


                                }
                            }
                            else
                            {
                                startActivity(new Intent(Login.this, ParentHomeScreen.class));
                            }
                            Toast.makeText(Login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
        );
    }



}
