package com.nghianguyen.intheneighborhood.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.nghianguyen.intheneighborhood.alert.RunProximityServiceReceiver;

public class ProximityServiceAlarmManager {
    private static ProximityServiceAlarmManager proximityServiceAlarmManager;

    private static final int PENDING_INTENT_ID = 900000;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private ProximityServiceAlarmManager(Context context){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RunProximityServiceReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, PENDING_INTENT_ID, intent, 0);
    }

    public static ProximityServiceAlarmManager get(Context context){
        if(proximityServiceAlarmManager == null){
            proximityServiceAlarmManager =
                    new ProximityServiceAlarmManager(context.getApplicationContext());
        }

        return proximityServiceAlarmManager;
    }

    public void setAlarmOn(boolean yes){
        if(yes){
            turnOnAlarm();
        }else{
            clearAlarm();
        }
    }

    public void turnOnAlarm(){
        clearAlarm();
        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis(),
                60000 * 1,
                pendingIntent);
    }

    public void clearAlarm(){
        alarmManager.cancel(pendingIntent);
    }
}
