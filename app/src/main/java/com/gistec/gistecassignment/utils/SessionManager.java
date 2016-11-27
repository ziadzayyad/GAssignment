package com.gistec.gistecassignment.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gistec.gistecassignment.model.Hospital;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ZAD on 11/24/2016.
 */

public class SessionManager {

    private static ArrayList<Hospital> hospitalsArray,savedHospitalsArray;
    private static SharedPreferences info;
    private static Context context;


    public SessionManager(Context context_){
        context = context_;
    }

    public static final int ALL_HOSPITALS_MODE = 0;
    public static final int SAVED_HOSPITALS_MODE = 1;
    public static final String HOSPITALS_MODE = "Mode";

    public static void setHospitalsArrayList(ArrayList<Hospital> hospitalsArray_)
    {
        hospitalsArray = hospitalsArray_;

    }

    public static ArrayList<Hospital> getHospitalsArrayList()
    {
        return hospitalsArray;
    }

    public static ArrayList<Hospital> getSavedHospitalsArrayList()
    {
        savedHospitalsArray= new ArrayList<Hospital>();
         info = context.getSharedPreferences("SavedHospitalsList",
                Context.MODE_PRIVATE);

        int counter = 0;
        for (Map.Entry<String, ?> entry : info.getAll().entrySet()) {

            counter++;
            /* String a = entry.getValue().toString();
            int b = Integer.parseInt(entry.getValue().toString());
            Hospital hospital = hospitalsArray.get(Integer.parseInt(entry.getValue().toString()));
            */
             savedHospitalsArray.add(hospitalsArray.get(Integer.parseInt(entry.getValue().toString())));
        }

        int a = counter;
        return savedHospitalsArray;
    }

    public static void addSavedHospitalToArrayList(Hospital savedHospital)
    {
       /* if(savedHospitalsArray.size()!=0)
        savedHospitalsArray = getSavedHospitalsArrayList();*/



    }
    public static void loadHospitalsImages()
    {

    }
}
