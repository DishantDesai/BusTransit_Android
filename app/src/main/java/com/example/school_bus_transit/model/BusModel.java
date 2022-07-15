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
}
