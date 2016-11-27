package com.gistec.gistecassignment.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.gistec.gistecassignment.model.Hospital;
import java.util.ArrayList;
import java.util.Map;

public class SessionManager
{
    private static ArrayList<Hospital> hospitalsArray,savedHospitalsArray,searchHospitalsResults;
    private static SharedPreferences info;
    private static Context context;
    public static final int ALL_HOSPITALS_MODE = 0;
    public static final int SAVED_HOSPITALS_MODE = 1;
    public static final int SEARCH_HOSPITALS_MODE = 2;
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
        for (Map.Entry<String, ?> entry : info.getAll().entrySet())
        {
             counter++;
             savedHospitalsArray.add(hospitalsArray.get(Integer.parseInt(entry.getValue().toString())));
        }
        int a = counter;
        return savedHospitalsArray;
    }

    public static void setSearchHospitalsArrayList(ArrayList<Hospital> searchHospitalsResults_)
    {
        searchHospitalsResults = searchHospitalsResults_;
    }

    public static ArrayList<Hospital> getSearchHospitalsArrayList()
    {
        return searchHospitalsResults;
    }

    public static void setContext(Context context_)
    {
        context = context_;
    }
}
