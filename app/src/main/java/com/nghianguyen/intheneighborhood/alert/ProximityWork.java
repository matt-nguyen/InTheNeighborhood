package com.nghianguyen.intheneighborhood.alert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;
import java.util.List;

import static com.nghianguyen.intheneighborhood.InTheNeightborhoodApp.CHANNEL_NEARBY_ALERT;

public class ProximityWork {

    // TODO: ProximityWork should have callbacks so that we don't have to pass ProximityService obj in here just to call jobFinished()

    private static final String LAST_LAT = "last_location_lat";
    private static final String LAST_LNG = "last_location_lng";

    private static final int MIN_MOVE_DISTANCE = 1;

    private ProximityAlertManager proximityAlertManager;

    private LocationCallback callback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Context context;

    public ProximityWork(final ProximityService proximityService, final JobParameters params){
        this.context = proximityService.getApplicationContext();
        this.fusedLocationProviderClient = new FusedLocationProviderClient(context);
        this.proximityAlertManager = ProximityAlertManager.get(context);

        this.callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                processForProximity(locationResult.getLastLocation());

                cleanup();
                proximityService.jobFinished(params, false);

            }
        };
    }

    public void startJob(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(1000)
                    .setFastestInterval(500)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, null);
        }
    }

    private void processForProximity(Location updatedLocation){
        showNotification(context, "Location - " + updatedLocation.getLatitude() + ","
                + updatedLocation.getLongitude(), 4);

        if(isEnoughMovement(updatedLocation)) {
            ArrayList<Task> tasks = TaskDbManager.get(context).getTasks();
            ArrayList<Task> withinProxyList = new ArrayList<>();
            ArrayList<Task> almostProxyList = new ArrayList<>();

            buildProxyLists(updatedLocation, tasks, withinProxyList, almostProxyList);

            showNotification(context, "Woah. there's enough movement. Within: "
                    + withinProxyList.size() + ", Close: " + almostProxyList.size(), 6);

            proximityAlertManager.addAllProximityAlerts(withinProxyList);
            proximityAlertManager.addAllProximityAlerts(almostProxyList);
        }

        saveLocation(updatedLocation);
    }

    private boolean isEnoughMovement(Location updatedLocation){
        Location previousLocation = getPreviousLocation();

        if(previousLocation != null) {
            float distance = previousLocation.distanceTo(updatedLocation);
            showNotification(context, "Distance since last - " + distance, 5);

            return distance > MIN_MOVE_DISTANCE;
        }

        return true;
    }


    private void buildProxyLists(Location currentLocation, ArrayList<Task> tasks,
                                 List<Task> withinProxyList, List<Task> almostProxyList){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        float proximityDistance = sharedPrefs.getFloat("pref_proximity_distance", 1f);

        LatLng locLatLng;
        Location taskLocation;
        float distanceFromTask;
        for (Task task : tasks) {
            if (task.isDone()) {
                continue;
            }

            locLatLng = task.getLocLatLng();

            if(locLatLng != null){
                taskLocation = new Location("");
                taskLocation.setLatitude(locLatLng.latitude);
                taskLocation.setLongitude(locLatLng.longitude);

                distanceFromTask = currentLocation.distanceTo(taskLocation) / 1609;

                if(proximityDistance >= distanceFromTask){
                    withinProxyList.add(task);
                }else if((proximityDistance + 0.5) >= distanceFromTask){
                    almostProxyList.add(task);
                }
            }

        }
    }

    private Location getPreviousLocation(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        float latitude = sharedPrefs.getFloat(LAST_LAT, -1);
        float longitude = sharedPrefs.getFloat(LAST_LNG, -1);

        boolean noLocation = latitude == -1 && longitude == -1;
        if(noLocation){
            return null;
        }else{
            Location previousLocation = new Location("");
            previousLocation.setLatitude(latitude);
            previousLocation.setLongitude(longitude);

            return previousLocation;
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
//        showNotification(context, "Cleanup", 10);
        if(fusedLocationProviderClient != null && callback != null){
            fusedLocationProviderClient.removeLocationUpdates(callback);
        }

        fusedLocationProviderClient = null;
        proximityAlertManager = null;
        callback = null;
        context = null;
    }

    private void showNotification(Context context, String content, int id){

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_NEARBY_ALERT)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("onReceive", "Sending notification");
        notificationManager.notify(id, notification);

    }
}
