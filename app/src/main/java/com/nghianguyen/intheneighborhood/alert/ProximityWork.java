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
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;

import static com.nghianguyen.intheneighborhood.InTheNeightborhoodApp.CHANNEL_NEARBY_ALERT;

public class ProximityWork {
    private LocationCallback callback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Context context;

    public ProximityWork(final ProximityService proximityService, final JobParameters params){
        this.context = proximityService.getApplicationContext();
        this.fusedLocationProviderClient = new FusedLocationProviderClient(context);

        this.callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                showNotification(context, "Location - " + lastLocation.getLatitude() + "," + lastLocation.getLongitude(), 4);

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                float distanceMiles = sharedPrefs.getFloat("pref_proximity_distance", 1f);
                showNotification(context, "distanceMiles - " + distanceMiles, 5);

                ArrayList<Task> tasks = TaskDbManager.get(context).getTasks();

                showNotification(context, "task count - " + tasks.size(), 6);

                cleanup();
                proximityService.jobFinished(params, false);
            }
        };
    }

    public void runJob(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(1000)
                    .setFastestInterval(500)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, null);
        }
    }

    public void cleanup(){
        showNotification(context, "Cleanup", 10);
        if(fusedLocationProviderClient != null && callback != null){
            fusedLocationProviderClient.removeLocationUpdates(callback);
        }

        fusedLocationProviderClient = null;
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
