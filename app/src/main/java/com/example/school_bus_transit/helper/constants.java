package com.example.school_bus_transit.helper;

import com.example.school_bus_transit.model.BusModel;
import com.example.school_bus_transit.model.SchoolModel;
import com.example.school_bus_transit.model.UserModel;
import java.util.ArrayList;
import java.util.List;

public class constants {
    public final static String DRIVER = "driver";
    public final static String PARENT = "parent";
    public static UserModel CurrentUser;
    public static BusModel CurrentBus;
    public static SchoolModel CurrentSchool;
    public static List<SchoolModel> allschool=new ArrayList<>();
    public static List<UserModel> alldriver=new ArrayList<>();
    public static List<BusModel> allbus=new ArrayList<>();
}
