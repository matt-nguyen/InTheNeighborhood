package com.unlimitedrice.intheneighborhood;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by unlim on 12/28/2016.
 */

public class AlertReceiver extends BroadcastReceiver {

    private static final String TAG = AlertReceiver.class.getName();

    private static final float PROXIMITY_ALERT_RADIUS = 1609;
    public static final String PROXIMITY_ALERT_INTENT =
            "com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT";

    public static final String EXTRA_TASK_ID =
            "com.unlimitedrice.intheneighborhood.task_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        Log.d(TAG, "isEntering - " + isEntering);

        // Send notification that device is within proximity of a task location
        if(isEntering){

            // Build intent to go to the task on click of notification
            Intent i = new Intent(context, TaskActivity.class);

            UUID taskId = (UUID)intent.getSerializableExtra(TaskActivity.EXTRA_TASK_ID);
            i.putExtra(EXTRA_TASK_ID, taskId);

            PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.content_entered_location))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.d(TAG, "Sending notification");
            notificationManager.notify(0, notification);

        }
    }

    /****************************************************************************
     * Adds a proximity alert for a task unless it is done or it has no location
     ****************************************************************************/
    public static void toggleProximityAlert(Context c, Task task){

        if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager =
                    (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

            int alertId = task.getAlertId();
            if(alertId == -1){
                alertId = TaskManager.get(c).generateAlertId();
                task.setAlertId(alertId);
            }

            // Build pending intent for the proximity alert
            PendingIntent pi = buildAlertPendingIntent(c, task);

            LatLng latLng = task.getLocLatLng();

            // Add/Remove proximity alert from location manager
            if (!task.isDone() && latLng != null) {
                Log.d(TAG, "Adding/Updating proximity alert");
                locationManager.addProximityAlert(latLng.latitude,
                        latLng.longitude,
                        PROXIMITY_ALERT_RADIUS,
                        -1,
                        pi);

            } else {
                Log.d(TAG, "Removing proximity alert");
                locationManager.removeProximityAlert(pi);
                pi.cancel();
            }
        }
    }

    /************************************************************************
     * Removes alerts from location manager for tasks that have been deleted
     ************************************************************************/
    public static void clearAlerts(Context c, ArrayList<Task> tasks){

        LocationManager locationManager =
                (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            PendingIntent pi;
            for (Task task : tasks) {
                if (task.getAlertId() != -1) {
                    pi = buildAlertPendingIntent(c, task);
                    Log.d(TAG, "Removing alert from task - " + task.getDescription());
                    locationManager.removeProximityAlert(pi);
                    pi.cancel();
                }
            }
        }else{
            Log.i(TAG, "Unable to remove proximity alerts. Need to get ACCESS_FINE_LOCATION");
        }

    }


    private static PendingIntent buildAlertPendingIntent(Context c, Task task){
        Intent intent = new Intent(c, TaskActivity.class);
        intent.setAction(PROXIMITY_ALERT_INTENT);
        intent.putExtra(EXTRA_TASK_ID, task.getId());

        return PendingIntent.getBroadcast(c, task.getAlertId(), intent, 0);
    }
}
