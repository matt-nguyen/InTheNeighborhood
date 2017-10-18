package com.nghianguyen.intheneighborhood.map;

import android.support.v7.app.AppCompatActivity;

public class GoogleApiConnectActivity extends AppCompatActivity {

    @Override
    public void onStart(){
        super.onStart();
        if (!GoogleServiceManager.get(this).getClient().isConnected()) {
            GoogleServiceManager.get(this).getClient().connect();
        }

    }

    @Override
    public void onStop(){
        if(GoogleServiceManager.get(this).getClient().isConnected()) {
            GoogleServiceManager.get(this).getClient().disconnect();
        }
        super.onStop();
    }

}
