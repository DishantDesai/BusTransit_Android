package com.example.school_bus_transit.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.TextView;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.SchoolAdapter;
import com.example.school_bus_transit.adapter.busListAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class schoolDetails extends AppCompatActivity implements Serializable
{
    SchoolModel school ;
    TextView schoolName , schoolEmail , schoolMo , schoolAddress  , busHeading;
    private RecyclerView recyclerViewSchoolList;
    private RecyclerView.Adapter busAdapter;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_details);
        String school_id = (String) getIntent().getSerializableExtra("school_id");

        for(int i=0;i<constants.allschool.size();i++)
        {
            if(school_id.equals(constants.allschool.get(i).getschool_id()))
            {
                school = constants.allschool.get(i);
            }
        }

        schoolName = findViewById(R.id.admin_school_schoolName);
        schoolEmail = findViewById(R.id.schoolEmailVal);
        schoolMo = findViewById(R.id.schoolMobVal);
        schoolAddress = findViewById(R.id.schoolAddressVal);
        busHeading = findViewById(R.id.admin_school_bus_heading);
        mContext=getApplicationContext();

        setData();
    }

    void setData()
    {
        List<BusModel> bus = new ArrayList<>();
        schoolName.setText(school.getname());
        schoolEmail.setText(school.getemail_id());
        schoolMo.setText(school.getphone_no());
        schoolAddress.setText(school.getaddress());

        for(int i=0;i<constants.allbus.size();i++)
        {
            if(constants.allbus.get(i).getschool_id().equals(school.getschool_id()))
            {
                bus.add(constants.allbus.get(i));
            }
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewSchoolList =  findViewById(R.id.admin_bus_list_recycle_view);
        recyclerViewSchoolList.setVerticalScrollBarEnabled(false);
        if(bus.size()!=0)
        {
            recyclerViewSchoolList.setLayoutManager(linearLayoutManager);
            busAdapter = new busListAdapter(mContext, (ArrayList<BusModel>) bus);
            recyclerViewSchoolList.setAdapter(busAdapter);
            recyclerViewSchoolList.setVisibility(View.VISIBLE);
        }
        else
        {
            busHeading.setVisibility(View.INVISIBLE);
        }


    }


}