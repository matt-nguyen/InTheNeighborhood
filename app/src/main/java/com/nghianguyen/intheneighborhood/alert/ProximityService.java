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

    private ProximityCheckJob work;

    @Override
    public boolean onStartJob(final JobParameters params) {
        work = new ProximityCheckJob(getApplicationContext(), new ProximityCheckJob.Callback() {
            @Override
            public void jobFinished() {
                ProximityService.this.jobFinished(params, false);
            }
        });
        work.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        work.cleanup();
        work = null;

        return false;
    }
}
