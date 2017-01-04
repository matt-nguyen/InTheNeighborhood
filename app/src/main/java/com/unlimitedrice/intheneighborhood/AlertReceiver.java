package com.unlimitedrice.intheneighborhood;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.UUID;

/**
 * Created by unlim on 12/28/2016.
 */

public class AlertReceiver extends BroadcastReceiver {

    private static final String TAG = "AlertReceiver";

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
}
