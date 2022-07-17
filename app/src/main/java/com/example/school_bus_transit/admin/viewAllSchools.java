package com.example.school_bus_transit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.SchoolAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.SchoolModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_schools);

        progressBar = (ProgressBar)findViewById(R.id.school_loading);
        mContext=getApplicationContext();
        getSchool();
    }

    public void getSchool()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewSchoolList =  findViewById(R.id.school_list_recycle_view);

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