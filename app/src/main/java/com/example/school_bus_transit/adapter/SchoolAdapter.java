package com.example.school_bus_transit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.school_bus_transit.R;
import com.example.school_bus_transit.model.SchoolModel;

import java.io.Serializable;
import java.util.ArrayList;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.ViewHolder> implements Serializable {
    ArrayList<SchoolModel> school;
    private Context context;
    public SchoolAdapter(Context context, ArrayList<SchoolModel> school) {
        this.school = school;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        System.out.println("parent "+ parent);
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sampleschooldata, parent, false);
        return new ViewHolder (inflate);
    }

    public void updateData(ArrayList<SchoolModel> matchFood)
    {
        this.school = matchFood;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        holder.title.setText(school.get(position).getname());

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


        holder.pic.setImageResource(R.drawable.logo);

    }
    @Override
    public int getItemCount() {
        return school.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.schoolName);
            pic = itemView.findViewById(R.id.school_item_image);
        }
    }
}
