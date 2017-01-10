package com.unlimitedrice.intheneighborhood;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by unlim on 12/21/2016.
 */

public class GoogleApiConnectActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "GoogleApiConnectActivit";

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private PendingIntent mPendingIntent;

    @Override
    public void onResume() {
        super.onStart();
        Log.d("GoogleApiConnectActivit", "Connecting to google client");
        GoogleServiceManager.get(this).getClient().connect();
        buildGoogleApiClient();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("GoogleApiConnectActivit", "Disconnecting from google client");
        GoogleServiceManager.get(this).getClient().disconnect();
        if(mGoogleApiClient != null) {
            if(mPendingIntent != null) {
                Log.d(TAG, "Stopping location update service calls");
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, mPendingIntent);
            }

            mGoogleApiClient.disconnect();
        }
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
        Log.d(TAG, "GoogleApiClient ocnnected. Building location request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting location services");

            Intent intent = new Intent(this, LocationUpdateService.class);
            mPendingIntent = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    mPendingIntent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location has been changed");

        if (location != null) {
            Log.d(TAG, "Lat/long - " + location.getLatitude() + ", " + location.getLongitude());
        }
    }

}
