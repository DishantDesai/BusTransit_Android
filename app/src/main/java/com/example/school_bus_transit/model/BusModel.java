package com.example.school_bus_transit.model;

import java.util.List;

public class BusModel {
    private Boolean active_sharing;
    private Boolean going_to_school;
    private String bus_id;
    private String bus_number;
    private String current_lat;
    private String current_long;
    private String destination;
    private String destination_lat;
    private String destination_long;
    private String school_id;
    private String source;
    private String source_lat;
    private String source_long;

    public BusModel(
            Boolean active_sharing,
            Boolean going_to_school,
            String bus_id,
            String bus_number,
            String current_lat,
            String current_long,
            String destination,
            String destination_lat,
            String destination_long,
            String school_id,
            String source,
            String source_lat,
            String source_long
    ){
        this.active_sharing = active_sharing;
        this.going_to_school = going_to_school;
        this.school_id = school_id;
        this.bus_id = bus_id;
        this.bus_number = bus_number;
        this.current_lat = current_lat;
        this.current_long = current_long;
        this.destination = destination;
        this.destination_lat = destination_lat;
        this.destination_long = destination_long;
        this.school_id = school_id;
        this.source = source;
        this.source_lat=source_lat;
        this.source_long=source_long;
    }

    public String getbus_number() {
        return bus_number;
    }

    public void setbus_number(String bus_number) {
        this.bus_number = bus_number;
    }

    public String getschool_id() {
        return school_id;
    }

    public void setschool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getbus_id() {
        return bus_id;
    }

    public void setbus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getcurrent_lat() {
        return current_lat;
    }

    public void setcurrent_lat(String current_lat) {
        this.current_lat = current_lat;
    }

    public String getdestination_lat() {
        return destination_lat;
    }

    public void setdestination_lat(String destination_lat) {
        this.destination_lat = destination_lat;
    }

    public String getdestination_long() {
        return destination_long;
    }

    public void setdestination_long(String destination_long) {
        this.destination_long = destination_long;
    }


    public String getsource_lat() {
        return source_lat;
    }

    public void setsource_lat(String source_lat) {
        this.source_lat = source_lat;
    }

    public String getsource_long() {
        return source_long;
    }

    public void setsource_long(String source_long) {
        this.source_long = source_long;
    }



    public String getcurrent_long() {
        return current_long;
    }

    public void setcurrent_long(String current_long) {
        this.current_long = current_long;
    }

    public String getdestination() {
        return destination;
    }

    public void setdestination(String destination) {
        this.destination = destination;
    }

    public String getsource() {
        return source;
    }

    public void setsource(String source) {
        this.source = source;
    }

    public Boolean getactive_sharing() {
        return active_sharing;
    }

    public void setactive_sharing(Boolean active_sharing) {
        this.active_sharing = active_sharing;
    }

    public Boolean getgoing_to_school() {
        return going_to_school;
    }

    public void setgoing_to_school(Boolean going_to_school)
    {
        this.going_to_school = going_to_school;
    }

}
