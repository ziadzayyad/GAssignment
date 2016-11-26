package com.gistec.gistecassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gistec.gistecassignment.adapter.HospitalsAdapter;
import com.gistec.gistecassignment.model.Hospital;
import com.gistec.gistecassignment.utils.SessionManager;

import java.util.ArrayList;

public class AllHospitalsActivity extends AppCompatActivity {

    private ListView lvHospitals;
    private HospitalsAdapter hospitalsAdapter;
    private ArrayList <Hospital> hospitalsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_hospitals);

        hospitalsArrayList = SessionManager.getHospitalsArrayList();

        lvHospitals = (ListView)findViewById(R.id.lvHospitals);

        hospitalsAdapter = new HospitalsAdapter(AllHospitalsActivity.this, hospitalsArrayList);
        Toast.makeText(AllHospitalsActivity.this, "hospitals length=" +hospitalsArrayList.size() , Toast.LENGTH_SHORT).show();
        lvHospitals.setAdapter(hospitalsAdapter);

        lvHospitals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(AllHospitalsActivity.this, "clicked on " + i , Toast.LENGTH_SHORT).show();
                Intent hospitalDetailsIntent = new Intent(AllHospitalsActivity.this,HospitalDetailsActivity.class);
                hospitalDetailsIntent.putExtra("hospitalID",i);
                startActivity(hospitalDetailsIntent);
            }
        });





    }
}
