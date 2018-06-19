package com.nghianguyen.intheneighborhood.alert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nghianguyen.intheneighborhood.R;

import static com.nghianguyen.intheneighborhood.InTheNeightborhoodApp.CHANNEL_NEARBY_ALERT;

public class ProximityService extends JobService {

    private ProximityWork work;

    @Override
    public boolean onStartJob(final JobParameters params) {
        work = new ProximityWork(this, params);
        work.startJob();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        showNotification(getApplicationContext(), "onStopJob", 2);

        work.cleanup();
        work = null;

        return false;
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
