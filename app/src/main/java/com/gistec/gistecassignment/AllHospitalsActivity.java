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
import com.gistec.gistecassignment.utils.SessionManager;

public class AllHospitalsActivity extends AppCompatActivity {

    private ListView lvHospitals;
    private HospitalsAdapter hospitalsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_hospitals);

        lvHospitals = (ListView)findViewById(R.id.lvHospitals);

        hospitalsAdapter = new HospitalsAdapter(AllHospitalsActivity.this, SessionManager.getHospitalsArrayList());
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
