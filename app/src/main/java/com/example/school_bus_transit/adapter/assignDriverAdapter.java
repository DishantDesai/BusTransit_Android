package com.example.school_bus_transit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.school_bus_transit.Login;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.admin.admin_assign_driver;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class assignDriverAdapter extends RecyclerView.Adapter<assignDriverAdapter.ViewHolder> implements Serializable {
    ArrayList<UserModel> driver;
    private Context context;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    public assignDriverAdapter(Context context, ArrayList<UserModel> driver) {
        this.driver = driver;
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
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sampledriverassign, parent, false);
        return new ViewHolder (inflate);
    }

    public void updateData(ArrayList<UserModel> matchFood)
    {
        this.driver = matchFood;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position)
    {

        holder.driver_name.setText(driver.get(position).getfullName().toString());
        holder.allocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  String driverId = "";

                Map<String,Object> school_update = new HashMap<>();
                school_update.put("bus_id",constants.CurrentBus.getbus_id());
                fStore.collection("User").document(driver.get(position).getuser_id()).update(school_update);
                Toast.makeText(context, "Driver assigned successfully", Toast.LENGTH_SHORT).show();
//                ((admin_assign_driver)context).finish();


            }
        });

//        Glide.with(holder.itemView.getContext())
//                .load("")
//                .into(holder.pic);

    }
    @Override
    public int getItemCount() {
        return driver.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView driver_name , allocate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driver_name = (TextView) itemView.findViewById(R.id.admin_assign_driver_name);
            allocate = itemView.findViewById(R.id.admin_assign_driver_allocate);
        }
    }
}
