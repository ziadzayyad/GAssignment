package com.gistec.gistecassignment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gistec.gistecassignment.model.Hospital;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private EditText mSearchEditText;
    private Button tsssssst;
    private Hospital hospital;
    private ArrayList<Hospital> hospitalsArrayList;

    private MarkerOptions markerOptions;
    private Marker marker;
    private String deviceLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



       // Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
       // myToolbar.setSubtitleTextColor(R.color.colorPrimary);
       // setSupportActionBar(myToolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        View searchRef = menu.findItem(R.id.action_search).getActionView();
        mSearchEditText = (EditText) searchRef.findViewById(R.id.searchText);



        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                   // onSearchButtonClicked(mSearchEditText);
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);

        //markerOptions = new MarkerOptions().position(sydney).title("Marker in Sydney");
       /* marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(-34, 151))
                .title("My Spot")
                .snippet("This is my spot!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // Toast.makeText(MapActivity.this, "markerInfo= " + marker.getSnippet() + "  " + marker.getTitle(), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapActivity.this, "onInfoWindowClick" + marker.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });

        deviceLanguage = Locale.getDefault().getDisplayLanguage();



        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        getHospitalsMarkers();
    }

    private void getHospitalsMarkers()
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://devmaps.mopw.gov.ae/arcgis/rest/services/mopw/mopw_projects/MapServer/find?searchText=e&contains=true&layers=1&returnGeometry=true&returnZ=false&returnM=false&f=pjson";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray resultsJsonArray = response.getJSONArray("results");
                            hospitalsArrayList = new ArrayList<Hospital>();
                            for (int i = 0; i<resultsJsonArray.length(); i++) {
                              hospital = new Hospital();
                              hospital.name_English = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("English_Name");
                              hospital.name_Arabic = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Arabic_Name");
                              hospital.type = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Type");
                              hospital.marker =  mMap.addMarker(new MarkerOptions()
                                      .position(new LatLng(resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getDouble("y"), resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getDouble("x")))
                                      .snippet(hospital.type)
                                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                if(deviceLanguage.equals("العربية")){
                                    hospital.marker.setTitle(hospital.name_Arabic);
                                }else
                                {
                                    hospital.marker.setTitle(hospital.name_English);
                                }



                              hospitalsArrayList.add(hospital);
                          }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospital.marker.getPosition(),6f));
                            Toast.makeText(MapActivity.this, "arraylistLength = " + hospitalsArrayList.size(), Toast.LENGTH_LONG).show();

                          //  Toast.makeText(MapActivity.this, "random hospital info = " + hospitalsArrayList.get(0).name_English + "  x=" + hospitalsArrayList.get(0).x + "  y=" + hospitalsArrayList.get(0).y, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapActivity.this, "Error in response", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsObjRequest);
    }


    private void moveCameraToUAE() {


    }



}
