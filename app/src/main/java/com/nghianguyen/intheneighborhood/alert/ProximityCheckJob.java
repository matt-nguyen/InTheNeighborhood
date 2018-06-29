package com.nghianguyen.intheneighborhood.alert;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ProximityCheckJob {
    private static final String LAST_LAT = "last_location_lat";
    private static final String LAST_LNG = "last_location_lng";

    private static final int MIN_MOVE_DISTANCE = 100;

    private ProximityAlertManager proximityAlertManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private Context context;
    private Callback callback;

    public ProximityCheckJob(Context context, final Callback callback){
        this.context = context.getApplicationContext();
        this.fusedLocationProviderClient = new FusedLocationProviderClient(this.context);
        this.proximityAlertManager = ProximityAlertManager.get(this.context);
        this.callback = callback;

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location updatedLocation = locationResult.getLastLocation();

                checkForProximityAlerts(updatedLocation);
                saveLocation(updatedLocation);

                cleanup();

                callback.jobFinished();
            }
        };
    }

    public void execute(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(1000)
                    .setFastestInterval(500)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }else{
            cleanup();
            callback.jobFinished();
        }
    }

    private void checkForProximityAlerts(Location updatedLocation){

        if(isEnoughMovementSinceLast(updatedLocation)) {
            ArrayList<Task> tasks = TaskDbManager.get(context).getTasks();
            ArrayList<Task> withinProxyList = new ArrayList<>();
            ArrayList<Task> almostProxyList = new ArrayList<>();

            buildProxyLists(updatedLocation, tasks, withinProxyList, almostProxyList);

            proximityAlertManager.addAllProximityAlerts(withinProxyList);
            proximityAlertManager.addAllProximityAlerts(almostProxyList);
        }
    }

    private boolean isEnoughMovementSinceLast(Location updatedLocation){
        Location previousLocation = getPreviousLocation();

        if(previousLocation != null) {
            float distance = previousLocation.distanceTo(updatedLocation);

            return distance > MIN_MOVE_DISTANCE;
        }

        return true;
    }


    private void buildProxyLists(Location currentLocation, ArrayList<Task> tasks,
                                 List<Task> withinProxyList, List<Task> almostProxyList){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        float proximityDistance = sharedPrefs.getFloat("pref_proximity_distance", 1f);

        for (Task task : tasks) {
            determineTask(task, proximityDistance, currentLocation,
                    withinProxyList, almostProxyList);
        }
    }

    private void determineTask(Task task, float proximityDistance, Location currentLocation,
                               List<Task> withinProxyList, List<Task> almostProxyList){
        if (task.isDone()) {
            return;
        }

        LatLng locLatLng = task.getLocLatLng();

        if(locLatLng != null){
            Location taskLocation = buildLocation(locLatLng.latitude, locLatLng.longitude);

            float distanceFromTask = currentLocation.distanceTo(taskLocation) / 1609;

            if(proximityDistance >= distanceFromTask){
                withinProxyList.add(task);
            }else if((proximityDistance + 0.5) >= distanceFromTask){
                almostProxyList.add(task);
            }
        }
    }

    private Location getPreviousLocation(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        float latitude = sharedPrefs.getFloat(LAST_LAT, -1);
        float longitude = sharedPrefs.getFloat(LAST_LNG, -1);

        boolean noPreviousLocationSaved = latitude == -1 && longitude == -1;
        if(noPreviousLocationSaved){
            return null;
        }else{
            return buildLocation(latitude, longitude);
        }
    }

    private void saveLocation(Location location){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putFloat(LAST_LAT, (float)location.getLatitude())
                .putFloat(LAST_LNG, (float)location.getLongitude())
                .apply();
    }


    public void cleanup(){
        if(fusedLocationProviderClient != null && locationCallback != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

        fusedLocationProviderClient = null;
        proximityAlertManager = null;
        locationCallback = null;
        context = null;
    }

    private Location buildLocation(double latitude, double longitude){
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    interface Callback{
        void jobFinished();
    }
}
