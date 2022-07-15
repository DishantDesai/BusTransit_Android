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
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getphone_no() {
        return phone_no;
    }

    public void setphone_no(String phone_no) {
        this.phone_no = phone_no;
    }


    public String getschool_id() {
        return school_id;
    }

    public void setschool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getemail_id() {
        return email_id;
    }

    public void setemail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String address) {
        this.address = address;
    }

    public String getlat() {
        return lat;
    }

    public void setlat(String lat) {
        this.lat = lat;
    }

    public String getlong() {
        return lng;
    }

    public void setlong(String lng) {
        this.lng = lng;
    }






}
