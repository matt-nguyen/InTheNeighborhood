package com.unlimitedrice.intheneighborhood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by unlim on 12/21/2016.
 */

public class GoogleApiConnectActivity extends AppCompatActivity {

    @Override
    public void onStart(){
        super.onStart();
//        Log.d("GoogleApiConnectActivit", "Connecting to google client");
        if (!GoogleServiceManager.get(this).getClient().isConnected()) {
            Log.d("TESTING", "connecting to google play");
            GoogleServiceManager.get(this).getClient().connect();
        }

    }

    @Override
    public void onStop(){
        super.onStop();
//        Log.d("GoogleApiConnectActivit", "Disconnecting from google client");
        if(GoogleServiceManager.get(this).getClient().isConnected()) {
            Log.d("TESTING", "disconnecting from google play");
            GoogleServiceManager.get(this).getClient().disconnect();
        }
    }

}
