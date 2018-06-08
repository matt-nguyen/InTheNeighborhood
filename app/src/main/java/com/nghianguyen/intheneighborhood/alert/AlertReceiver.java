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
import android.widget.Toast;

import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;

public class AlertReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "com.nghianguyen.intheneighborhood.task_id";
    public static final String EXTRA_TASK_DESC = "com.nghianguyen.intheneighborhood.task_desc";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        Log.d("onReceive", "isEntering - " + isEntering);

        Toast.makeText(context, "onReceive. isEntering - " + isEntering, Toast.LENGTH_LONG).show();

        if(isEntering){
            String taskDescription = intent.getStringExtra(EXTRA_TASK_DESC);

            Intent taskIntent = new Intent(context, TaskActivity.class);

            int taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
            taskIntent.putExtra(TaskActivity.EXTRA_TASK_ID, taskId);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, taskIntent, 0);


            showNotification(context, taskDescription, pendingIntent);
        }
    }

    private void showNotification(Context context, String content, PendingIntent pendingIntent){
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("onReceive", "Sending notification");
        notificationManager.notify(0, notification);

    }
}
