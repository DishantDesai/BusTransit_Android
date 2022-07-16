package com.example.school_bus_transit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.school_bus_transit.R;
import com.example.school_bus_transit.model.BusModel;

import java.io.Serializable;
import java.util.ArrayList;

public class busListAdapter extends RecyclerView.Adapter<busListAdapter.ViewHolder> implements Serializable {
    ArrayList<BusModel> school;
    private Context context;
    public busListAdapter(Context context, ArrayList<BusModel> school) {
        this.school = school;
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
        this.school = matchFood;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        holder.no.setText(school.get(position).getbus_number());

        if(school.get(position).getactive_sharing())
        {
            holder.status.setText("On");
        }
        else
        {
            holder.status.setText("Off");
        }
        holder.address.setText(school.get(position).getdestination());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, DishDetails.class);

//                intent.putExtra("dish",school.get(position));
//                intent.putExtra("position",position);

//                context.startActivity(intent);
            }
        });

//        Glide.with(holder.itemView.getContext())
//                .load("")
//                .into(holder.pic);

    }
    @Override
    public int getItemCount() {
        return school.size();
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
