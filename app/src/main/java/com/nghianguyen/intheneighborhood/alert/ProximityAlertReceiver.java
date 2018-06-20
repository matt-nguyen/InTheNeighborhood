package com.nghianguyen.intheneighborhood.alert;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;

import static com.nghianguyen.intheneighborhood.InTheNeightborhoodApp.CHANNEL_NEARBY_ALERT;

public class ProximityAlertReceiver  extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "com.nghianguyen.intheneighborhood.task_id";
    public static final String EXTRA_TASK_DESC = "com.nghianguyen.intheneighborhood.task_desc";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        Log.d("onReceive", "isEntering - " + isEntering);

//        showNotification(context, "testing receiver", null, false);

//        if(isEntering){
            String taskDescription = intent.getStringExtra(EXTRA_TASK_DESC);

            Intent taskIntent = new Intent(context, TaskActivity.class);

            int taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
            taskIntent.putExtra(TaskActivity.EXTRA_TASK_ID, taskId);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, taskId, taskIntent, 0);

            showNotification(context, taskId, taskDescription, pendingIntent, isEntering);
//        }
    }

    private void showNotification(Context context, int taskId, String content, PendingIntent pendingIntent, boolean isEntering) {

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_NEARBY_ALERT)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("onReceive", "Sending notification");
        notificationManager.notify(taskId + 20, notification);

    }
}
