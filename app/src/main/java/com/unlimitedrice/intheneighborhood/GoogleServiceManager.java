package com.unlimitedrice.intheneighborhood;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class GoogleServiceManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = GoogleServiceManager.class.getName();

    private static GoogleServiceManager sGoogleApiClientObj;
    private static GoogleApiClient mGoogleApiClient;
    private Context mContext;

    private GoogleServiceManager(Context context){
        mContext = context;
        init();
    }

    public static GoogleServiceManager get(Context c){

        if(sGoogleApiClientObj == null){
            sGoogleApiClientObj = new GoogleServiceManager(c.getApplicationContext());
        }

        return sGoogleApiClientObj;

    }

    /***************************************
     * Returns the GoogleApiClient object
     * @return
     ***************************************/
    public GoogleApiClient getClient(){
        if(mGoogleApiClient == null){
            init();
        }

        return mGoogleApiClient;
    }

    /**********************************************
     * Initiate the Google Api Client for the app
     **********************************************/
    public void init(){
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /*************************************************************
     * Updates the lastLocation whenever the client is connected
     * @param bundle
     *************************************************************/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("GoogleServiceManager", "Getting last location");

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000)
                    .setFastestInterval(1000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting location services");

                Intent intent = new Intent(mContext, LocationUpdateService.class);
                PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        locationRequest,
                        pendingIntent);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
