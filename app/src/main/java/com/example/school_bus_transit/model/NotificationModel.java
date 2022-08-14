package com.example.school_bus_transit.model;

import java.util.Date;

public class NotificationModel {

    private String notification_id;
    private String bus_id;
    private String driver_id;
    private String school_id;
    private String title;
    private String message;
    private Date timestamp;

    public NotificationModel(String notification_id, String bus_id, String driver_id, String school_id, String title, String message, Date timestamp) {
        this.notification_id = notification_id;
        this.bus_id = bus_id;
        this.driver_id = driver_id;
        this.school_id = school_id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }






}
