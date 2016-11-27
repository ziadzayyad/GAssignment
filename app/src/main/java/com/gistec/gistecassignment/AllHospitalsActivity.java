package com.gistec.gistecassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    private int mode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_hospitals);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        if (bundle != null){

            mode = intent.getIntExtra(SessionManager.HOSPITALS_MODE, 0);
         }
        if (mode == SessionManager.ALL_HOSPITALS_MODE)
        {
            hospitalsArrayList = SessionManager.getHospitalsArrayList();



        }
        else  // saved hospitals mode
        {

        }





        lvHospitals = (ListView)findViewById(R.id.lvHospitals);

        setTitle(R.string.title_activity_map);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
