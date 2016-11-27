package com.gistec.gistecassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gistec.gistecassignment.model.Hospital;
import com.gistec.gistecassignment.utils.SessionManager;
import java.util.ArrayList;

public class HospitalDetailsActivity extends AppCompatActivity
{
    private TextView tvType, tvOwnerShip,tvBeneficiary,tvFinancenumber,tvWorkingHoursAM,tvWorkingHoursPM,tvArea;
    private ImageView ivBackButton,ivbSaveHospital;
    private NetworkImageView niHospitalImage;
    private ArrayList<Hospital> hospitalsArray;
    private Integer HospitalNum;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private TextView tvName;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);
        initiateVariables();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            HospitalNum = intent.getIntExtra("hospitalID", 0);
            mode = intent.getIntExtra(SessionManager.HOSPITALS_MODE, 0);
        }

        if (mode == SessionManager.ALL_HOSPITALS_MODE)
        {
            hospitalsArray = SessionManager.getHospitalsArrayList();
        }
        else if(mode == SessionManager.SAVED_HOSPITALS_MODE)
        {
            hospitalsArray = SessionManager.getSavedHospitalsArrayList();
        }
        else if(mode == SessionManager.SEARCH_HOSPITALS_MODE)
        {
            hospitalsArray = SessionManager.getSearchHospitalsArrayList();
        }
        setHospitalDetails(hospitalsArray.get(HospitalNum));
        ivBackButton .setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
        ivbSaveHospital .setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(!checkHospitalIfSaved())
                {
                    saveHospitalDetails(hospitalsArray.get(HospitalNum));
                    Toast.makeText(HospitalDetailsActivity.this, "Hospital is saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HospitalDetailsActivity.this, "Hospital is already saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkHospitalIfSaved() {
        return false;
    }

    private void saveHospitalDetails(Hospital hospital) {

        SharedPreferences info = this.getSharedPreferences("SavedHospitalsList",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = info.edit();
        prefsEditor.putInt(""+hospital.hospitalID,hospital.hospitalID);
        prefsEditor.apply();
    }

    private void initiateVariables()
    {
        tvName = (TextView)findViewById(R.id.tvName);
        tvType = (TextView) findViewById(R.id.tvType);
        tvOwnerShip= (TextView) findViewById(R.id.tvOwnership);
        tvBeneficiary= (TextView) findViewById(R.id.tvBeneficiary);
        tvFinancenumber= (TextView) findViewById(R.id.tvFinancenumber);
        tvWorkingHoursAM= (TextView) findViewById(R.id.tvWorkingHoursAM);
        tvWorkingHoursPM= (TextView) findViewById(R.id.tvWorkingHoursPM);
        tvArea= (TextView) findViewById(R.id.tvArea);
        ivBackButton = (ImageView)findViewById(R.id.ivBackfDetails);
        ivbSaveHospital = (ImageView)findViewById(R.id.ivbSaveHospital);
        niHospitalImage= (NetworkImageView) findViewById(R.id.niHospitalImage);
        requestQueue = Volley.newRequestQueue(HospitalDetailsActivity.this);
        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }
    private void setHospitalDetails(Hospital hospital)
    {
        tvName.setText(hospital.name);
        tvType.setText(hospital.type);
        tvOwnerShip.setText(hospital.ownerShip);
        tvBeneficiary.setText(hospital.beneficiary);
        tvFinancenumber.setText(hospital.financeNumber);
        tvWorkingHoursAM.setText(hospital.workingHoursAM);
        tvWorkingHoursPM.setText(hospital.workingHoursPM);
        tvArea.setText(hospital.area);
        if(!hospital.imageUrl.equals("Null"))
        niHospitalImage.setImageUrl(hospital.imageUrl, imageLoader);
    }
}
