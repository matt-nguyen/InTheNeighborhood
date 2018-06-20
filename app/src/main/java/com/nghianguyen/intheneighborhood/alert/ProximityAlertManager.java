package com.nghianguyen.intheneighborhood.alert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;

public class ProximityAlertManager {

    private static ProximityAlertManager proximityAlertManager;

    private static final int METERS_PER_MILE = 1609;

    private LocationManager locationManager;
    private Context context;

    private ProximityAlertManager(Context context){
        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public static ProximityAlertManager get(Context context){
        if(proximityAlertManager == null){
            proximityAlertManager = new ProximityAlertManager(context.getApplicationContext());
        }

        return proximityAlertManager;
    }

//    public void updateAllProximityAlerts(ArrayList<Task> tasks){
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//
//        if(sharedPrefs.getBoolean("pref_gps", false)){
//            addAllProximityAlerts(tasks);
//        }else{
//            removeAllProximityAlerts(tasks);
//        }
//
//    }

    public void addProximityAlert(Task task, long id){
        if(id > -1){
            addTaskProximityAlert(task);
        }
    }

    public void addProximityAlert(Task task){
        if(task.getDb_id() > -1){
            addTaskProximityAlert(task);
        }
    }

    public void addAllProximityAlerts(ArrayList<Task> tasks){
        for (Task task : tasks) {
                addTaskProximityAlert(task);
        }
    }

    private void addTaskProximityAlert(Task task){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if(task.getLocLatLng() != null) {
                PendingIntent pendingIntent = buildPendingIntent(task);

                locationManager.removeProximityAlert(pendingIntent);

                locationManager.addProximityAlert(
                        task.getLocLatLng().latitude,
                        task.getLocLatLng().longitude,
                        getProximityDistance(),
                        60000 * 5,
                        pendingIntent
                );
            }
        }
    }

    public void removeAllProximityAlerts(ArrayList<Task> tasks){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(context, RunProximityServiceReceiver.class);
            for (Task task : tasks) {
                locationManager.removeProximityAlert(
                        PendingIntent.getBroadcast(context, task.getDb_id(), intent, 0)
                );
            }
        }
    }

    public void removeProximityAlert(Task t, long id){
        LatLng locLatLng = t.getLocLatLng();
        if(id > -1 && locLatLng != null){
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(context, RunProximityServiceReceiver.class);
                locationManager.removeProximityAlert(
                        PendingIntent.getBroadcast(context, (int) id, i, 0)
                );

            }
        }
    }

    private PendingIntent buildPendingIntent(Task task){
        Intent intent = new Intent(context, ProximityAlertReceiver.class);

        intent.putExtra(ProximityAlertReceiver.EXTRA_TASK_ID, task.getDb_id());
        intent.putExtra(ProximityAlertReceiver.EXTRA_TASK_DESC, task.getDescription());

        return PendingIntent.getBroadcast(context, task.getDb_id(), intent, 0);
    }

    private float getProximityDistance(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        float distanceMiles = sharedPrefs.getFloat("pref_proximity_distance", 1f);

        return METERS_PER_MILE * distanceMiles;
    }

}
