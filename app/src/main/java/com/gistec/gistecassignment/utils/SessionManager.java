package com.gistec.gistecassignment.utils;

import com.gistec.gistecassignment.model.Hospital;

import java.util.ArrayList;

/**
 * Created by ZAD on 11/24/2016.
 */

public class SessionManager {

    private static ArrayList<Hospital> hospitalsArray;

    public static void setHospitalsArrayList(ArrayList<Hospital> hospitalsArray_)
    {
        hospitalsArray = hospitalsArray_;
    }

    public static ArrayList<Hospital> getHospitalsArrayList()
    {
        return hospitalsArray;
    }
    public static void loadHospitalsImages()
    {

    }
}
