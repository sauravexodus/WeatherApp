package com.exodus.ashraf.weatherapp;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    double latitude,longitude;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    CollapsingToolbarLayout collapsingToolbar;
    TextView temperature, humidity, pressure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();

        /* CardView Declarations */
        temperature = (TextView)findViewById(R.id.tempText);
        humidity = (TextView)findViewById(R.id.humidText);
        pressure = (TextView)findViewById(R.id.PresText);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/font.ttf");
        temperature.setTypeface(typeface);
        humidity.setTypeface(typeface);
        pressure.setTypeface(typeface);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        loadBackdrop();

        //Loading the internet content

    }

    private void FetchWeather(){
        String OpenUrl = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+
                +longitude+"&units=metric";
        //Toast.makeText(getApplicationContext(),OpenUrl,Toast.LENGTH_LONG).show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, OpenUrl,(String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONObject jsonObject = response.getJSONObject("name");
                            collapsingToolbar.setTitle(response.getString("name"));
                            temperature.setText(response.getJSONObject("main").getString("temp") + "°C");
                            pressure.setText(response.getJSONObject("main").getString("pressure")+ " hPa");
                            humidity.setText(response.getJSONObject("main").getString("humidity")+ "%");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.cover).centerCrop().into(imageView);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
           latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();
            //Toast.makeText(getApplication(),latitude+" and "+longitude,Toast.LENGTH_SHORT).show();
            FetchWeather();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/
}
