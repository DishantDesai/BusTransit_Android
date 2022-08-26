package com.example.school_bus_transit.helper;

import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import com.example.school_bus_transit.model.parentScreenModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class constants {
    public final static String DRIVER = "DRIVER";
    public final static String PARENT = "PARENT";
    public static UserModel CurrentUser,CurrentSelectedDriver;
    public static BusModel CurrentBus = new BusModel(
            false,
            false,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    );
    public static SchoolModel CurrentSchool;
    public static List<SchoolModel> allschool=new ArrayList<>();
    public static List<UserModel> alldriver=new ArrayList<>();
    public static List<BusModel> allbus=new ArrayList<>();
    public static List<parentScreenModel> parentScreendata=new ArrayList<>();
    public static List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
}
