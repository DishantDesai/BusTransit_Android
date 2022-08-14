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

    public String getbus_id() {
        return bus_id;
    }

    public String getuser_id() {
        return user_id;
    }
    public String getgender() {
        return gender;
    }

    public String getfullName() {
        return fullName;
    }

    public String getphone_no() {
        return phone_no;
    }

    public String getphoto_url() {
        return photo_url;
    }
    public String getemail_id() {
        return email_id;
    }

    public String getuser_lat() {
        return user_lat;
    }
    public String getuser_long() {
        return user_long;
    }

    public String getaddress() {
        return address;
    }

    public List<String> getschool_id() {
        return school_id;
    }

}
