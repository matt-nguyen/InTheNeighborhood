package com.nghianguyen.intheneighborhood.map;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
