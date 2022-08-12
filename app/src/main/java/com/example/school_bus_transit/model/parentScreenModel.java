package com.example.school_bus_transit.model;

public class parentScreenModel {

    private String driverName;
    private String driverImage;
    private String bus_id;
    private String busNumber;
    private String phone_no;
    private boolean toSchool;

    public parentScreenModel(String driverName, String phone_no, String bus_id, String driverImage, String busNumber, boolean toSchool){
        this.driverName = driverName;
        this.phone_no = phone_no;
        this.bus_id = bus_id;
        this.driverImage = driverImage;
        this.busNumber = busNumber;
        this.toSchool = toSchool;
    }

    public String getname() {
        return driverName;
    }

    public void setname(String driverName) {
        this.driverName = driverName;
    }

    public String getphone_no() {
        return phone_no;
    }

    public void setphone_no(String phone_no) {
        this.phone_no = phone_no;
    }


    public String getbus_id() {
        return bus_id;
    }

    public void setbus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getdriverImage() {
        return driverImage;
    }

    public void setdriverImage(String driverImage) {
        this.driverImage = driverImage;
    }

    public String getbusNumber() {
        return busNumber;
    }

    public void setbusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Boolean gettoSchool() {
        return toSchool;
    }

    public void settoSchool(Boolean toSchool) {
        this.toSchool = toSchool;
    }

}
