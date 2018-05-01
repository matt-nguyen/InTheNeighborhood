package com.nghianguyen.intheneighborhood.alert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;

public class ProximityAlertManager {

    public static final String ACTION_PROXIMITY_ALERT =
            "com.nghianguyen.intheneighborhood.PROXIMITY_ALERT";

    private LocationManager locationManager;
    private Context context;

    public ProximityAlertManager(Context context){
        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public void updateAllProximityAlerts(ArrayList<Task> tasks){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(sharedPrefs.getBoolean("pref_gps", false)){
            addAllProximityAlerts(tasks);
        }else{
            removeAllProximityAlerts(tasks);
        }

    }

    public void addProximityAlert(Task t, long id){
        LatLng locLatLng = t.getLocLatLng();
        if(id > -1 && locLatLng != null){
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(ACTION_PROXIMITY_ALERT);
                i.putExtra(AlertReceiver.EXTRA_TASK_ID, t.getDb_id());
                i.putExtra(AlertReceiver.EXTRA_TASK_DESC, t.getDescription());

                PendingIntent pi = PendingIntent.getBroadcast(context, (int) id, i, 0);

                locationManager.removeProximityAlert(pi);

                locationManager.addProximityAlert(
                        locLatLng.latitude,
                        locLatLng.longitude,
                        getProximityDistance(),
                        -1,
                        pi
                );
            }
        }
    }

    public void addAllProximityAlerts(ArrayList<Task> tasks){
        Log.d("TESTING", "adding all proximity alerts");
        Log.d("TESTING", "proximity distance - " + getProximityDistance());
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent i;
            PendingIntent pi;
            LatLng locLatLng;
            for (Task task : tasks) {
                locLatLng = task.getLocLatLng();

                if(locLatLng != null) {
                    i = new Intent(ACTION_PROXIMITY_ALERT);
                    i.putExtra(AlertReceiver.EXTRA_TASK_ID, task.getDb_id());
                    i.putExtra(AlertReceiver.EXTRA_TASK_DESC, task.getDescription());

                    pi = PendingIntent.getBroadcast(context, task.getDb_id(), i, 0);

                    locationManager.removeProximityAlert(pi);

                    locationManager.addProximityAlert(
                            locLatLng.latitude,
                            locLatLng.longitude,
                            getProximityDistance(),
                            -1,
                            pi
                    );
                }
            }
        }
    }

    public void removeProximityAlert(Task t, long id){
        LatLng locLatLng = t.getLocLatLng();
        if(id > -1 && locLatLng != null){
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(ACTION_PROXIMITY_ALERT);
                locationManager.removeProximityAlert(
                        PendingIntent.getBroadcast(context, (int) id, i, 0)
                );

            }
        }
    }

    public void removeAllProximityAlerts(ArrayList<Task> tasks){
        Log.d("TESTING", "removing all proximity alerts");
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent i = new Intent(ACTION_PROXIMITY_ALERT);
            for (Task task : tasks) {
                locationManager.removeProximityAlert(
                        PendingIntent.getBroadcast(context, task.getDb_id(), i, 0)
                );

            }
        }
    }

    private float getProximityDistance(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int distanceMiles = sharedPrefs.getInt("pref_distance", 1);

        return 1609 * distanceMiles;
    }

}
