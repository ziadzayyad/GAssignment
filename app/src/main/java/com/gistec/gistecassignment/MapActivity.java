package com.gistec.gistecassignment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    private EditText mSearchEditText;
    private Hospital hospital;
    private ArrayList<Hospital> hospitalsArrayList,searchHospitalsArraylist;
    HashMap<Marker, Integer> hospitaMarkerlHashMap = new HashMap<Marker, Integer>();
    private String deviceLanguage;
    private ProgressDialog progressDialog;
    private boolean isPermissionsLoaded = false;
    private  InputMethodManager imm;
    LatLng latLng;
    Marker currLocationMarker;
    private LocationRequest mLocationRequest;
    private GoogleApiClient client;
    private RequestQueue queue;
    private JsonObjectRequest jsObjRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        } else {
            onPermissionsSuccess();
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
    }

    private void onPermissionsSuccess() {

        setContentView(R.layout.activity_map);

        SessionManager.setContext(MapActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

                    if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                        return true;

                    mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {

                                imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                            }
                        }
                    });

                    searchTrigger();
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    private void searchTrigger()
    {
        SessionManager.setSearchHospitalsArrayList(searchHospitalName(mSearchEditText.getText().toString()));

        if (searchHospitalsArraylist.size()!= 0 && !searchHospitalsArraylist.isEmpty()) {

            Intent allHospitalsIntent = new Intent(MapActivity.this, AllHospitalsActivity.class);
            allHospitalsIntent.putExtra(SessionManager.HOSPITALS_MODE, SessionManager.SEARCH_HOSPITALS_MODE);
            startActivity(allHospitalsIntent);
        } else
        {
            Toast.makeText(MapActivity.this, "no Hospital found with this name please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Hospital> searchHospitalName(String searchName)
    {
        searchHospitalsArraylist = new ArrayList<Hospital>();
        searchName = searchName.toLowerCase();
        int searchListLength = hospitalsArrayList.size();
        for (int i = 0; i < searchListLength; i++)
        {
            if (searchName != null && !searchName.isEmpty() && !searchName.equals(" ") && hospitalsArrayList.get(i).name.toLowerCase().contains(searchName)) {
                searchHospitalsArraylist.add(hospitalsArrayList.get(i));
            }
        }
        return searchHospitalsArraylist;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_ShowHospitalsList:
                Intent allHospitalsIntent = new Intent(MapActivity.this, AllHospitalsActivity.class);
                allHospitalsIntent.putExtra(SessionManager.HOSPITALS_MODE, SessionManager.ALL_HOSPITALS_MODE);
                startActivity(allHospitalsIntent);
                return true;
            case R.id.action_search:
                mSearchEditText.setText("");
                mSearchEditText.requestFocus();
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus)
                        {
                            imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                        }
                    }
                });
                return true;

            case R.id.action_ShowSavedPlaces:
                Intent savedHospitalsIntent = new Intent(MapActivity.this, AllHospitalsActivity.class);
                savedHospitalsIntent.putExtra(SessionManager.HOSPITALS_MODE, SessionManager.SAVED_HOSPITALS_MODE);
                startActivity(savedHospitalsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        client.connect();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(!marker.equals(currLocationMarker))
                {
                Intent hospitalDetailsIntent = new Intent(MapActivity.this, HospitalDetailsActivity.class);
                int hospitalID = hospitaMarkerlHashMap.get(marker);
                hospitalDetailsIntent.putExtra("hospitalID", hospitalID);
                startActivity(hospitalDetailsIntent);
                }
            }
        });
        deviceLanguage = Locale.getDefault().getDisplayLanguage();
        progressDialog = ProgressDialog.show(MapActivity.this, "Loading", "please wait...");
        putHospitalsMarkers();
    }

    private void putHospitalsMarkers() {
        queue = Volley.newRequestQueue(this);
        String url = "http://devmaps.mopw.gov.ae/arcgis/rest/services/mopw/mopw_projects/MapServer/find?searchText=e&contains=true&layers=1&returnGeometry=true&returnZ=false&returnM=false&f=pjson";
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray resultsJsonArray = response.getJSONArray("results");
                            hospitalsArrayList = new ArrayList<Hospital>();

                            for (int i = 0; i < resultsJsonArray.length(); i++) {
                                hospital = new Hospital();
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
                                hospital.hospitalID = i; // Hospital unique identifier
                                if (deviceLanguage.equals("العربية"))
                                {
                                    hospital.name = hospital.name_Arabic;
                                } else
                                {
                                    hospital.name = hospital.name_English;
                                }
                                hospital.marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getDouble("y"), resultsJsonArray.getJSONObject(i).getJSONObject("geometry").getDouble("x")))
                                        .snippet(hospital.type).title(hospital.name)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                hospitaMarkerlHashMap.put(hospital.marker, i);

                                hospitalsArrayList.add(hospital);
                            }
                            SessionManager.setHospitalsArrayList(hospitalsArrayList);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospital.marker.getPosition(), 6f));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        new AlertDialog.Builder(MapActivity.this)
                                .setTitle("Network Error")
                                .setMessage("Please check your internet connection and try again")
                                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.show();
                                        queue.add(jsObjRequest);
                                    }
                                })
                                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                        progressDialog.dismiss();
                    }
                });
        queue.add(jsObjRequest);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Map Page")
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
        if (isPermissionsLoaded) {
            client.connect();
            AppIndex.AppIndexApi.start(client, getIndexApiAction());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPermissionsLoaded) {
            AppIndex.AppIndexApi.end(client, getIndexApiAction());
            client.disconnect();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsSuccess();
                }
                else
                {
                    Toast.makeText(MapActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    showPermissionConfirmationDialog();
                }
                return;
            }
        }
    }

    private void showPermissionConfirmationDialog(){
        new android.app.AlertDialog.Builder(MapActivity.this)
                .setTitle("location Permission Denied ")
                .setMessage("Are you sure you want to deny location permission?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onSearchButtonClicked(View view)
    {
        searchTrigger();
    }
}
