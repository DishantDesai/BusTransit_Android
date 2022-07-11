package com.example.school_bus_transit.model;

import java.util.List;

public class UserModel {
    private String user_id;
    private String photo_url;
    private String gender;
    private String bus_id;
    private String fullName;
    private String phone_no;
    private List<String> school_id;
    private String email_id;
    private String address;
    private String user_lat;
    private String user_long;
    private String user_type;

    public UserModel(
            String user_id,
            String photo_url,
            String gender,
            String bus_id,
            String fullName,
            String phone_no,
            List<String> school_id,
            String email_id,
            String address,
            String user_lat,
            String user_long,
            String user_type
    ){
        this.fullName = fullName;
        this.phone_no = phone_no;
        this.school_id = school_id;
        this.email_id = email_id;
        this.address = address;
        this.user_lat = user_lat;
        this.user_long = user_long;
        this.user_id = user_id;
        this.photo_url = photo_url;
        this.gender = gender;
        this.bus_id = bus_id;
        this.user_type = user_type;
    }
    public String getUserType() {
        return user_type;
    }
}
