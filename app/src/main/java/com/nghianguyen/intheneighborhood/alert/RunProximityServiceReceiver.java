package com.nghianguyen.intheneighborhood.alert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nghianguyen.intheneighborhood.R;

import static com.nghianguyen.intheneighborhood.InTheNeightborhoodApp.CHANNEL_NEARBY_ALERT;

public class RunProximityServiceReceiver extends BroadcastReceiver {
    private static final int JOB_ID = 10;
    @Override
    public void onReceive(Context context, Intent intent) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(JOB_ID, new ComponentName(context, ProximityService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build());
    }

}
