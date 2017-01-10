package com.unlimitedrice.intheneighborhood;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by unlim on 1/10/2017.
 */

public class LocationUpdateService extends IntentService {

    private static final String TAG = LocationUpdateService.class.getName();

    private final float PROXIMITY_ALERT_RADIUS = (float)0.5 * 1609; // 1609 = 1 mile

    public LocationUpdateService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(LocationResult.hasResult(intent)){
            Location userLocation = LocationResult.extractResult(intent).getLastLocation();

            // Check and send notification of any unfinished tasks within range of the user
            if(userLocation != null){
                Log.d(TAG, "User lat/lng - " + userLocation.getLatitude() + ", " + userLocation.getLongitude());

                StringBuilder sb = new StringBuilder();

                ArrayList<Task> tasks = TaskManager.get(this).getTasks();

                Location taskLocation = new Location("");
                LatLng taskLatLng;
                float distance;

                for(Task task : tasks){
                    Log.d(TAG, "Task - " + task.getDescription());
                    taskLatLng = task.getLocLatLng();

                    // Include task in notification if unfinished and has location
                    if(!task.isDone() && taskLatLng != null) {
                        taskLocation.setLatitude(taskLatLng.latitude);
                        taskLocation.setLongitude(taskLatLng.longitude);
                        Log.d(TAG, "Task lat/lng - " + taskLocation.getLatitude() + ", "
                                + taskLocation.getLongitude());

                        distance = userLocation.distanceTo(taskLocation);
                        Log.d(TAG, "distanceTo - " + distance);

                        if(distance < PROXIMITY_ALERT_RADIUS){
                            sb.append(task.getDescription() + "\n");
                            task.setNearby(true); // TODO: Testing
                        }else{
                            task.setNearby(false);
                        }

                    }else{
                        task.setNearby(false); // Make sure nearby is false here
                    }
                }

                if(sb.length() > 0){
                    sendNotification(sb.toString()); // TODO: Testing
                }

            }
        }
    }

    /*********************************************************************************
     * Sends notification of tasks nearby the user that takes them to the task list
     *********************************************************************************/
    private void sendNotification(String content){

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("There are tasks nearby:\n" + content)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "Sending notification");
        nm.notify(0, notification);
    }
}
