package com.unlimitedrice.intheneighborhood;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by unlim on 10/11/2017.
 */

public class ProximityAlertManager {

    private LocationManager locationManager;
    private Context context;

    public ProximityAlertManager(Context context){
        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public void addProximityAlert(Task t, long id){
        LatLng locLatLng = t.getLocLatLng();
        if(id > -1 && locLatLng != null){
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
                PendingIntent pi = PendingIntent.getBroadcast(context, (int) id, i, 0);

                locationManager.removeProximityAlert(pi);

                locationManager.addProximityAlert(
                        locLatLng.latitude,
                        locLatLng.longitude,
                        1609,
                        -1,
                        pi
                );
            }
        }
    }

    public void addAllProximityAlerts(ArrayList<Task> tasks){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent i = new Intent("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
            PendingIntent pi;
            LatLng locLatLng;
            for (Task task : tasks) {
                locLatLng = task.getLocLatLng();

                if(locLatLng != null) {
                    pi = PendingIntent.getBroadcast(context, task.getDb_id(), i, 0);

                    locationManager.removeProximityAlert(pi);

                    locationManager.addProximityAlert(
                            locLatLng.latitude,
                            locLatLng.longitude,
                            1609,
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

                Intent i = new Intent("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
                locationManager.removeProximityAlert(
                        PendingIntent.getBroadcast(context, (int) id, i, 0)
                );

            }
        }
    }

    public void removeProximityAlerts(ArrayList<Task> tasks){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent i = new Intent("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
            for (Task task : tasks) {
                locationManager.removeProximityAlert(
                        PendingIntent.getBroadcast(context, task.getDb_id(), i, 0)
                );

            }
        }
    }
}
