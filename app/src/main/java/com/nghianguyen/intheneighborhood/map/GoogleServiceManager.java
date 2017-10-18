package com.nghianguyen.intheneighborhood.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleServiceManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static GoogleServiceManager sGoogleApiClientObj;
    private static GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
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
    }

    /**************************************************
     * Returns the most recent Location of the device.
     **************************************************/
    public Location getLastLocation(){
        return lastLocation;
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

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}