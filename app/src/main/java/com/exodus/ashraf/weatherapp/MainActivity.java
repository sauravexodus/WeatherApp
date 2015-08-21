package com.exodus.ashraf.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    double latitude,longitude;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    CollapsingToolbarLayout collapsingToolbar;
    TextView temperature, humidity, pressure,weather,wind,windspeed;
    LinearLayout linearLayout;
    NestedScrollView nestedScrollView;
    String myImage, ImageUrl, myUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();

        /* CardView Declarations */
        temperature = (TextView)findViewById(R.id.tempText);
        humidity = (TextView)findViewById(R.id.humidText);
        pressure = (TextView)findViewById(R.id.PresText);
        weather = (TextView)findViewById(R.id.tempText2);
        wind = (TextView)findViewById(R.id.windText2);
        windspeed = (TextView)findViewById(R.id.windText);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/font.ttf");
        temperature.setTypeface(typeface);
        humidity.setTypeface(typeface);
        pressure.setTypeface(typeface);
        windspeed.setTypeface(typeface);

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        nestedScrollView = (NestedScrollView)findViewById(R.id.cards);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        makeCollapsingToolbarLayoutLooksGood(collapsingToolbar);

        loadBackdrop(null);

        //Loading the internet content

    }

    private void FetchWeather(){
        String OpenUrl = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+
                +longitude+"&units=metric";
        String PlaceUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+"," +
                longitude+"&sensor=false";
        ImageUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=weather%20";

        //Toast.makeText(getApplicationContext(),OpenUrl,Toast.LENGTH_LONG).show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, OpenUrl,(String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            linearLayout.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                            //collapsingToolbar.setTitle(response.getString("name"));
                            temperature.setText(response.getJSONObject("main").getString("temp") + "°C");
                            weather.setText(response.getJSONArray("weather").getJSONObject(0).getString("main"));
                            pressure.setText(response.getJSONObject("main").getString("pressure")+ " hPa");
                            humidity.setText(response.getJSONObject("main").getString("humidity") + "%");
                            windspeed.setText(response.getJSONObject("wind").getString("speed")+"m/sec");
                            wind.setText(response.getJSONObject("wind").getString("deg") + " degrees");
                            myUrl = ImageUrl + response.getJSONArray("weather").getJSONObject(0).
                                    getString("description");

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
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, PlaceUrl, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            collapsingToolbar.setTitle(response.getJSONArray("results").getJSONObject(1).
                                    getJSONArray("address_components").getJSONObject(1).getString("short_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Can't get accurate location"
                                    ,Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, myUrl, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            myImage = response.getJSONObject("responseData").getJSONArray("results").
                            getJSONObject(0).getString("url");
                            loadBackdrop(myImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Can't get photo"
                                    ,Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
        requestQueue.add(jsonObjectRequest1);
        requestQueue.add(jsonObjectRequest2);
    }

    private void loadBackdrop(String img) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        if(img!= null)
            Glide.with(this).load(img).centerCrop().into(imageView);
        else
            Glide.with(this).load(R.drawable.newmage).centerCrop().into(imageView);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
           latitude=mLastLocation.getLatitude();
            longitude=mLastLocation.getLongitude();
            //Toast.makeText(getApplication(),latitude+" and "+longitude,Toast.LENGTH_SHORT).show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FetchWeather(); //Do something after 100ms
                }
            }, 2000);
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
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {onConnected(null);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        latitude=mLastLocation.getLatitude();
        longitude=mLastLocation.getLongitude();
        FetchWeather();
    }

    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
           // ((TextPaint) tpf.get(object)).setColor(getResources().getColor(R.color.ice));
        } catch (Exception ignored) {
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/
}
