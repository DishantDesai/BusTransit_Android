package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.SchoolAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.SchoolModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class viewAllSchools extends AppCompatActivity implements Serializable  {

    private RecyclerView recyclerViewSchoolList;
    private RecyclerView.Adapter schoolAdapter;
    ProgressBar progressBar;
    static Context mContext;
    MenuItem menuItem;
    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_schools);
        getSupportActionBar().hide();

        topAppBar = (MaterialToolbar)findViewById(R.id.topAppBar);
        mContext=getApplicationContext();

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.add_school_icon:
                        // TODO
                        startActivity(new Intent(mContext, add_new_school.class));
                        break;
                    // TODO: Other cases
                }
                return true;
            }
        });



        progressBar = (ProgressBar)findViewById(R.id.school_loading);

        getSchool();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSchool();
    }

    public void getSchool()
    {


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewSchoolList =  findViewById(R.id.school_list_recycle_view);
        progressBar.setVisibility(View.VISIBLE);



        FirebaseFirestore.getInstance().collection("School")
                .get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {

                                    ArrayList<SchoolModel> school = new ArrayList<>();
                                    for(QueryDocumentSnapshot doc : task.getResult())
                                    {
                                        school.add (new SchoolModel(doc.getData().get("name").toString(),
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

                                    if(constants.allschool.size()!=0)
                                    {
                                        recyclerViewSchoolList.setLayoutManager(linearLayoutManager);
                                        schoolAdapter = new SchoolAdapter(mContext, (ArrayList<SchoolModel>) constants.allschool);
                                        recyclerViewSchoolList.setAdapter(schoolAdapter);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        recyclerViewSchoolList.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }

                                }
                            }
                        }
                );
    }
}