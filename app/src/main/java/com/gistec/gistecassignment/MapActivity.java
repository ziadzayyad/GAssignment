package com.gistec.gistecassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.gistec.gistecassignment.utils.SessionManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private EditText mSearchEditText;
    private Hospital hospital;
    private ArrayList<Hospital> hospitalsArrayList,savedHospitalsArrayList;
    SharedPreferences mPrefs;
    HashMap<Marker, Integer> hospitaMarkerlHashMap= new HashMap<Marker, Integer>();
    private MarkerOptions markerOptions;
    private Marker marker;
    private String deviceLanguage;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        View searchRef = menu.findItem(R.id.action_search).getActionView();
        mSearchEditText = (EditText) searchRef.findViewById(R.id.searchText);

        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    Hospital hospital_= searchHospitalName(mSearchEditText.getText().toString());

                    if(hospital_ !=null){


                        Intent hospitalDetailsIntent = new Intent(MapActivity.this,HospitalDetailsActivity.class);
                        hospitalDetailsIntent.putExtra("hospitalID",hospitaMarkerlHashMap.get(hospital_.marker));
                        startActivity(hospitalDetailsIntent);


                    }
                    // onSearchButtonClicked(mSearchEditText);
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    private Hospital searchHospitalName(String searchName) {

        int searchListLength = hospitalsArrayList.size();
        for(int i=0; i<searchListLength;i++){

            if(searchName!=null && hospitalsArrayList.get(i).name.contains(searchName)){
                return hospitalsArrayList.get(i);
            }
        }

        Toast.makeText(MapActivity.this, "no Hospital found with this name please try again", Toast.LENGTH_SHORT).show();



        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_ShowHospitalsList:
                Intent hospitalsIntent = new Intent(MapActivity.this, AllHospitalsActivity.class);
                SessionManager.setHospitalsArrayList(hospitalsArrayList);
                startActivity(hospitalsIntent);

                return true;

            case R.id.action_search:

                return true;

            case R.id.action_ShowSavedPlaces:

                savedHospitalsArrayList = getSavedHospitalsArrayList();
                //use AllHospitals Activity to display the Saved Hospitals Array List by parsing an intent with the Array list. Same for the Hospitals Array List

               /* Intent hospitalsIntent2 = new Intent(MapActivity.this, AllHospitalsActivity.class);
                SessionManager.setSavedHospitalsArrayList(savedHospitalsArrayList);
                startActivity(hospitalsIntent2);*/

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Hospital> getSavedHospitalsArrayList() {

        SharedPreferences info = this.getSharedPreferences("SavedHospitalsList",
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json;
        Hospital hospital;

        for (Map.Entry<String, ?> entry : info.getAll().entrySet()) {

          //  map.put(entry.getKey(), entry.getValue().toString());

           // json = info.getString("HospitalObject", "");
            json = entry.getValue().toString();
            savedHospitalsArrayList.add(hospital = gson.fromJson(json, Hospital.class));
        }





        return savedHospitalsArrayList;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Toast.makeText(MapActivity.this, "markerInfo= " + marker.getSnippet() + "  " + marker.getTitle(), Toast.LENGTH_LONG).show();
                return false;
            }
        });
*/
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //go to show Marker details
                Toast.makeText(MapActivity.this, "onInfoWindowClick" + marker.getTitle(), Toast.LENGTH_SHORT).show();

                Intent hospitalDetailsIntent = new Intent(MapActivity.this,HospitalDetailsActivity.class);
                int hospitalID =hospitaMarkerlHashMap.get(marker);
                hospitalDetailsIntent.putExtra("hospitalID",hospitalID);
                startActivity(hospitalDetailsIntent);


            }
        });

        deviceLanguage = Locale.getDefault().getDisplayLanguage();

        putHospitalsMarkers();
    }

    private void putHospitalsMarkers() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://devmaps.mopw.gov.ae/arcgis/rest/services/mopw/mopw_projects/MapServer/find?searchText=e&contains=true&layers=1&returnGeometry=true&returnZ=false&returnM=false&f=pjson";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray resultsJsonArray = response.getJSONArray("results");
                            hospitalsArrayList = new ArrayList<Hospital>();
                            hospital = new Hospital();
                            for (int i = 0; i < resultsJsonArray.length(); i++) {

                                hospital.name_English = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("English_Name");
                                hospital.name_Arabic = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Arabic_Name");
                                hospital.type = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Type");
                                hospital.imageUrl = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Imageurl");

                                hospital.ownerShip = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Ownership");
                                hospital.financeNumber = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Finance_No");
                                hospital.workingHoursAM = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Working_Hours_AM");
                                hospital.workingHoursPM = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Working_Hours_PM");
                                hospital.beneficiary = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Beneficiary_party_EN");
                                hospital.area = resultsJsonArray.getJSONObject(i).getJSONObject("attributes").getString("Area_EN");

                                if (deviceLanguage.equals("العربية")) {
                                 //   hospital.marker.setTitle(hospital.name_Arabic);
                                    hospital.name = hospital.name_Arabic;
                                } else {
                                  //  hospital.marker.setTitle(hospital.name_English);
                                    hospital.name = hospital.name_English;
                                }

                                // put Marker on the map
                                hospital.marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getDouble("y"), resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getDouble("x")))
                                        .snippet(hospital.type).title(hospital.name)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                hospitaMarkerlHashMap.put(hospital.marker,i);

                                hospitalsArrayList.add(hospital);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospital.marker.getPosition(), 6f));
                            Toast.makeText(MapActivity.this, "arraylistLength = " + hospitalsArrayList.size(), Toast.LENGTH_LONG).show();


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


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Map Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
