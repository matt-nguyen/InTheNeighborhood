package com.unlimitedrice.intheneighborhood;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by unlim on 12/21/2016.
 */

public class GoogleApiConnectActivity extends AppCompatActivity {

    @Override
    public void onStart(){
        super.onStart();
        Log.d("GoogleApiConnectActivit", "Connecting to google client");
        GoogleServiceManager.get(this).getClient().connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("GoogleApiConnectActivit", "Disconnecting from google client");
        GoogleServiceManager.get(this).getClient().disconnect();
    }
}
