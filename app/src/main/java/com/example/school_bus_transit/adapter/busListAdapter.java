package com.example.school_bus_transit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.admin.DriverBusInfo;
import com.example.school_bus_transit.helper.constants;
import com.example.school_bus_transit.model.BusModel;

import java.io.Serializable;
import java.util.ArrayList;

public class busListAdapter extends RecyclerView.Adapter<busListAdapter.ViewHolder> implements Serializable {
    ArrayList<BusModel> bus;
    private Context context;
    public busListAdapter(Context context, ArrayList<BusModel> bus) {
        this.bus = bus;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.samplebusdata, parent, false);
        return new ViewHolder (inflate);
    }

    public void updateData(ArrayList<BusModel> matchFood)
    {
        this.bus = matchFood;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        holder.no.setText(bus.get(position).getbus_number());

        if(bus.get(position).getactive_sharing())
        {
            holder.status.setText("On");
        }
        else
        {
            holder.status.setText("Off");
        }
        holder.address.setText(bus.get(position).getdestination());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  String driverId = "";

                  for(int i=0;i< constants.alldriver.size(); i++)
                  {
                      if(bus.get(position).getbus_id().equals(constants.alldriver.get(i).getbus_id()))
                      {
                          driverId = constants.alldriver.get(i).getuser_id();
                      }
                  }

                  if(!driverId.equals(""))
                  {
                    Intent intent = new Intent(context, DriverBusInfo.class);
                    intent.putExtra("driver_id",driverId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                  }
                  else
                  {



                  }

//                Intent intent = new Intent(context, DriverBusInfo.class);
//                intent.putExtra("driver_id",bus.get(position).);
//                intent.putExtra("position",position);

//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
            }
        });

//        Glide.with(holder.itemView.getContext())
//                .load("")
//                .into(holder.pic);

    }
    @Override
    public int getItemCount() {
        return bus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView no , status , address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.admin_school_bus_no);
            status = itemView.findViewById(R.id.admin_school_bus_status);
            address = itemView.findViewById(R.id.admin_school_bus_address);
        }
    }
}
