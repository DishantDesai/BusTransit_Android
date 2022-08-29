package com.example.school_bus_transit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.school_bus_transit.admin.adminDashBoard;
import com.example.school_bus_transit.admin.schoolDetails;
import com.example.school_bus_transit.driver.DriverHomeScreen;
import com.example.school_bus_transit.driver.driver_not_allowed_screen;
import com.example.school_bus_transit.helper.FirebaseHelper;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.UserModel;
import com.example.school_bus_transit.parents.ParentHomeScreen;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Login extends AppCompatActivity {
    private TextInputLayout email,pass;
    private Button loginBtn;
    TextView signup;
    ImageView googleSignIn;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        googleSignIn = findViewById(R.id.google_signIn);

//        email.getEditText().setText("driver@gmail.com");
//        pass.getEditText().setText("Driver@123");

        email.getEditText().setText("parent@gmail.com");
        pass.getEditText().setText("Parent@123");

        loginBtn=findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //Initialise google authenticator object
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1001136823104-rbh8tvs5un0ol2m7ee8dr57hftp9ib0v.apps.googleusercontent.com")
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this,gso);

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

        //Google Signin
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSigInHandler();
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

    private void googleSigInHandler(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if(task.isSuccessful()){
                try {
                    GoogleSignInAccount account= task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
            }

        }

    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credentials = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credentials).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    shouldAddUser();
                }else{
                    Toast.makeText(Login.this, "Login with google Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void shouldAddUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        CollectionReference usersRef = fStore.collection("User");
        Query query = usersRef.whereEqualTo("user_id", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size() == 0 ){
                        //ToDO:Redirect to registration screen with user details
                        Intent intent = new Intent(Login.this, Registration.class);

                        intent.putExtra("user_id",user.getUid());
                        intent.putExtra("name",user.getDisplayName());
                        intent.putExtra("email",user.getEmail());
                        intent.putExtra("photo_url",user.getPhotoUrl());
                        startActivity(intent);
                    }else{
                        userRedirect();
                    }
                }

            }
        });
    }
}
