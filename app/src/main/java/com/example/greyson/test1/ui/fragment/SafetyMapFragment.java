package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;


public class SafetyMapFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = FragmentActivity.class.getSimpleName();
    //private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private Spinner spinner;
    private int changeState;
    MapView mapView;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_safetymap, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
                googleMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomBy(13));
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)        // 1 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds


        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (pos == 0) {
                    changeState = 1;
                }
                if (pos == 1) {
                    changeState = 2;
                }
                if (pos == 2) {
                    changeState = 3;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        spinnerFunction(location);
    }


    private void selectLocation(Location location, boolean range) {
        if (!compareLocation(location, 2000, range)) {
            compareLocation(location, 5000, range);
        }
    }

    public boolean compareLocation(Location location, int dis, boolean range) {

        String json = "[{\"id\":1,\"establishment\":\"McDonald's\",\"address\":\"1500 Eastlink Northbound\",\"suburb\":\"Scoresby\",\"postcode\":3179,\"state\":\"VIC\",\"type\":\"Restaurant\",\"latitude\":\"-37.9123\",\"longtitude\":\"145.2135\"},{\"id\":2,\"establishment\":\"McDonald's\",\"address\":\"Princes Fwy\",\"suburb\":\"Officer\",\"postcode\":3809,\"state\":\"VIC\",\"type\":\"Restaurant\",\"latitude\":\"-38.0608\",\"longtitude\":\"145.4134\"},{\"id\":3,\"establishment\":\"McDonald's\",\"address\":\"127 Canterbury Rd\",\"suburb\":\"Blackburn South\",\"postcode\":3130,\"state\":\"VIC\",\"type\":\"Restaurant\",\"latitude\":\"-37.8316\",\"longtitude\":\"145.1478\"},{\"id\":4,\"establishment\":\"McDonald's\",\"address\":\"Burwood Hwy & Scott Grove\",\"suburb\":\"Burwood\",\"postcode\":3125,\"state\":\"VIC\",\"type\":\"Restaurant\",\"latitude\":\"-37.8506\",\"longtitude\":\"145.0980\"},{\"id\":5,\"establishment\":\"McDonald's\",\"address\":\"606 Warrigal Rd\",\"suburb\":\"Holmesglen\",\"postcode\":3148,\"state\":\"VIC\",\"type\":\"Restaurant\",\"latitude\":\"-37.8759\",\"longtitude\":\"145.0910\"},{\"id\":6,\"establishment\":\"McDonald's\",\"address\":\"North Rd & Poath Road\",\"suburb\":\"Oakleigh\",\"postcode\":3166,\"state\":\"VIC\",\"type\":\"Restaurant\",\"latitude\":\"-37.9081\",\"longtitude\":\"145.0742\"}]";
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = jsonObject.getString("establishment");
                double lai = Double.parseDouble(jsonObject.getString("latitude"));
                double lon = Double.parseDouble(jsonObject.getString("longtitude"));
                if (getDistance(handleNewLocation(location).latitude, handleNewLocation(location).longitude, lai, lon) < dis) {
                    addMark(lai, lon, name);
                    range = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return range;
    }

    private LatLng handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        return latLng;
    }

    public void addMark(double c, double d, String name) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(c, d)).title(name));
    }

    public double getDistance(double a, double b, double c, double d) {
        float[] results = new float[1];
        Location.distanceBetween(a, b, c, d, results);
        return results[0];
    }

    public void spinnerFunction(Location location) {
        boolean range = false;
        selectLocation(location, range);
        if (changeState == 2) {
        googleMap.clear();
        handleNewLocation(location);
        }
        if (changeState == 3) {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.clear();
        spinnerFunction(location);
    }

    /**@Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            changeState = 1;
        }
        if (position == 2) {
            changeState = 2;
        }
        if (position == 3) {
            changeState = 3;
        }
        //mGoogleApiClient.connect();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        changeState = 1;
    }*/
}

