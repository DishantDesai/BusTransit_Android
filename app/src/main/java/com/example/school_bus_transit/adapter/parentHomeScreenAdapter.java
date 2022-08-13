package com.example.school_bus_transit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.admin.DriverBusInfo;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.parentScreenModel;
import com.example.school_bus_transit.model.parentScreenModel;
import com.example.school_bus_transit.parents.busTrack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class parentHomeScreenAdapter extends RecyclerView.Adapter<parentHomeScreenAdapter.ViewHolder> implements Serializable {
    ArrayList<parentScreenModel> parentScreendata;
    private Context context;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    public parentHomeScreenAdapter(Context context, ArrayList<parentScreenModel> parentScreendata) {
        this.parentScreendata = parentScreendata;
        this.context = context;
        //Firebase Object Initialisation
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_parent_home, parent, false);
        return new ViewHolder (inflate);
    }

    public void updateData(ArrayList<parentScreenModel> matchFood)
    {
        this.parentScreendata = matchFood;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position)
    {

        holder.driver_name.setText(parentScreendata.get(position).getname().toString());
//        holder.bus_status.setText(parentScreendata.get(position).getfullName().toString());
        holder.bus_number.setText(parentScreendata.get(position).getbusNumber().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, busTrack.class);
                intent.putExtra("bus_id",parentScreendata.get(position).getbus_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        
        Glide.with(holder.itemView.getContext())
                .load(parentScreendata.get(position).getdriverImage().toString())
                .into(holder.driver_image);

    }
    @Override
    public int getItemCount() {
        return parentScreendata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView driver_name , driver_number , bus_number, bus_status;
        ImageView driver_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driver_name = (TextView) itemView.findViewById(R.id.parent_homeScreen_driver_name);
            driver_number = (TextView) itemView.findViewById(R.id.parent_homeScreen_driver_number);
            bus_number = (TextView) itemView.findViewById(R.id.parent_homeScreen_bus_number);
            bus_status = (TextView) itemView.findViewById(R.id.parent_homeScreen_bus_status);
            driver_image = itemView.findViewById(R.id.parent_homeScreen_driver_image);

           
        }
    }
}
