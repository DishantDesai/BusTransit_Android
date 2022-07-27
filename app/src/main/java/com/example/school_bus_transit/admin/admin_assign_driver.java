package com.example.school_bus_transit.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.adapter.assignDriverAdapter;
import com.example.school_bus_transit.adapter.busListAdapter;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class admin_assign_driver extends AppCompatActivity {

    RecyclerView recyclerViewDriverList;
    assignDriverAdapter assignAdapter;
    Context mContext;
    TextView school_name;
    TextView school_address;
    TextView end_address;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assign_driver);
        mContext=getApplicationContext();

        setData();
    }

    void setData()
    {
        school_name = (TextView) findViewById(R.id.admin_assign_driver_school_name) ;
        school_address = (TextView) findViewById(R.id.admin_assign_address_text) ;
        end_address = (TextView) findViewById(R.id.admin_assign_end_address_text) ;

        school_name.setText(constants.CurrentSchool.getname());
        school_address.setText(constants.CurrentBus.getsource());
        end_address.setText(constants.CurrentBus.getdestination());

        List<UserModel> driver = new ArrayList<>();

        for(int i = 0; i< constants.alldriver.size(); i++)
        {
            if(constants.alldriver.get(i).getbus_id().equals(""))
            {
                driver.add(constants.alldriver.get(i));
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewDriverList =  findViewById(R.id.admin_assign_driver_recycle_view);
        recyclerViewDriverList.setVerticalScrollBarEnabled(false);
        if(driver.size()!=0)
        {
            recyclerViewDriverList.setLayoutManager(linearLayoutManager);
            assignAdapter = new assignDriverAdapter(mContext, (ArrayList<UserModel>) driver);
            recyclerViewDriverList.setAdapter(assignAdapter);
            recyclerViewDriverList.setVisibility(View.VISIBLE);
        }

    }

    
}