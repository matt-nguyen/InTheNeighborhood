package com.unlimitedrice.intheneighborhood;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class GoogleApiConnectActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "GoogleApiConnectActivit";

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private PendingIntent mPendingIntent;

    @Override
    public void onResume() {
        super.onStart();
        Log.d(TAG, "Connecting to google client");
        GoogleServiceManager.get(this).getClient().connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Disconnecting from google client");
        GoogleServiceManager.get(this).getClient().disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.d(TAG, "GoogleApiClient ocnnected. Building location request");
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000)
//                .setFastestInterval(1000)
//                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "Requesting location services");
//
//            Intent intent = new Intent(this, LocationUpdateService.class);
//            mPendingIntent = PendingIntent.getService(this, 0, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//                    mLocationRequest,
//                    mPendingIntent);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
