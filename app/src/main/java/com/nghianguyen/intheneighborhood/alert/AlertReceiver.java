package com.nghianguyen.intheneighborhood.alert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;

public class AlertReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "com.nghianguyen.intheneighborhood.task_id";
    public static final String EXTRA_TASK_DESC = "com.nghianguyen.intheneighborhood.task_desc";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        Log.d("onReceive", "isEntering - " + isEntering);

        // Send notification that device is within proximity of a task location
        if(isEntering){

            // Build intent to go to the task on click of notification
            Intent i = new Intent(context, TaskActivity.class);

            int taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
            i.putExtra(TaskActivity.EXTRA_TASK_ID, taskId);

            PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

            String taskDescription = intent.getStringExtra(EXTRA_TASK_DESC);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(taskDescription)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.d("onReceive", "Sending notification");
            notificationManager.notify(0, notification);

        }
    }
}
