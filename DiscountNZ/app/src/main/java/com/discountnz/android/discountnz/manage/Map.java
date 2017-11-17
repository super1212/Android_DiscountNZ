package com.discountnz.android.discountnz.manage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.discountnz.android.discountnz.R;
import com.discountnz.android.discountnz.model.Product;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    SeekBar seekBar;
    int distance = 0;

    List <Product>productList = new ArrayList<Product>();;
    LatLng ll;
    RequestQueue queue = null;
    public RequestQueue getRequestQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        return queue;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getProductsInfo();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        seekBar();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
            }// TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera



    }
    LocationRequest mLocationRequest;





    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setInterval(1000);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    @Override
    public void onLocationChanged(Location location) {


        System.out.println("report");

        if(location != null){

            ll = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15);

            mMap.animateCamera(update);


            showShops();
        }

    }


    Marker marker;
    public void showShops(){


//        for(int i = 0; i < productList.size();i++){
//            LatLng shopLocation = new LatLng((Product)productList[i].getLatitude,);
//        }



        for(Product pro : productList){
            mMap.clear();
            System.out.println(productList.size());
            LatLng shopLocation = new LatLng(Double.parseDouble(pro.getLatitude()),Double.parseDouble(pro.getLongitude()));
            if(CalculationByDistance(shopLocation,ll)<distance){
                setMarker(pro.getBrand(),shopLocation.latitude,shopLocation.longitude);
            }
        }



    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult * 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    private void getProductsInfo(){
        //get the request queue
        queue = getRequestQueue(getApplicationContext());
        //current response url
        String url = "https://gist.githubusercontent.com/super1212/d0a3131282534ebe60b581b8a7f7be1f/raw/products.json";
        //use get method to get the json result
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //get information form the json object
                        try {
                            JSONArray dataList = response.getJSONArray("data");
                            if(dataList != null){
                                for(int i = 0; i < dataList.length(); i++){
                                    JSONObject myJson = dataList.getJSONObject(i);
                                    if(myJson == null){
                                        continue;
                                    }
                                    Product productItem = new Product();
                                    productItem.setAddr((String) myJson.get("addr"));
                                    productItem.setBrand((String) myJson.get("brand"));
                                    productItem.setCategory((String) myJson.get("category"));
                                    productItem.setEndDate((String) myJson.get("endDate"));
                                    productItem.setImgUrl((String) myJson.get("imgUrl"));
                                    productItem.setLatitude((String) myJson.get("latitude"));
                                    productItem.setLongitude((String) myJson.get("longitude"));
                                    productItem.setName((String) myJson.get("name"));
                                    productItem.setPrice((String) myJson.get("price"));
                                    productItem.setStartDate((String) myJson.get("startDate"));
                                    productList.add(productItem);


                                    System.out.println(i);
                                }
                                System.out.println("showGridData");


                                System.out.println("showGridData######");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.err.println(error.getMessage());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }


    private void setMarker(String locality, double lat, double lng) {
        if(marker != null){
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat,lng));
        marker = mMap.addMarker(options);
    }


    private void seekBar(){
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        progress = i;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        distance = progress / 10;
                        System.out.println(distance);
                        showShops();
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15-distance/3);
                        mMap.animateCamera(update);
                    }
                }
        );
    }

}