package com.example.school_bus_transit.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

public class SchoolModel {
    private String name;
    private String phone_no;
    private String school_id;
    private String email_id;
    private String address;
    private String lat;
    private String lng;

    public SchoolModel(String name, String phone_no, String school_id, String email_id, String address, String lat, String lng){
        this.name = name;
        this.phone_no = phone_no;
        this.school_id = school_id;
        this.email_id = email_id;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

}
