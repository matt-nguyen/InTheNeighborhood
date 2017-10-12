package com.unlimitedrice.intheneighborhood;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by unlim on 9/21/2017.
 */

public class TaskDbManager {
    private static TaskDbManager taskDbManager;
    private TaskOpenHelper dbHelper;
//    private LocationManager locationManager;
//    private Context context;
    private ProximityAlertManager proximityAlertManager;

    private TaskDbManager(Context context){
//        this.context = context;
        this.dbHelper = new TaskOpenHelper(context);
//        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.proximityAlertManager = new ProximityAlertManager(context);
    }

    public static TaskDbManager get(Context context){
        if(taskDbManager == null){
            taskDbManager = new TaskDbManager(context);
        }

        return taskDbManager;
    }

    public ArrayList<Task> getTasks(){
        return dbHelper.getTasks();
    }

    public Task getTask(int id){
        return dbHelper.getTask(id);
    }

    public void addTask(Task t){
        long id = dbHelper.addTask(t);
//        addProximityAlert(t, id);
        proximityAlertManager.addProximityAlert(t, id);
    }

    public void updateTask(Task t){
        long id = dbHelper.updateTask(t);
//        addProximityAlert(t, id);
        proximityAlertManager.addProximityAlert(t, id);
    }

    public void clearTasks(){
        dbHelper.clearTasks();
//        removeProximityAlerts();
        proximityAlertManager.removeProximityAlerts(dbHelper.getTasks());
    }

//    private void addProximityAlert(Task t, long id){
//        LatLng locLatLng = t.getLocLatLng();
//        if(id > -1 && locLatLng != null){
//            Intent i = new Intent("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
//            PendingIntent pi = PendingIntent.getBroadcast(context, (int) id, i, 0);
//            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//
//                locationManager.removeProximityAlert(pi);
//
//                locationManager.addProximityAlert(
//                        locLatLng.latitude,
//                        locLatLng.longitude,
//                        1609,
//                        -1,
//                        pi
//                );
//            }
//        }
//    }
//
//    private void removeProximityAlerts(){
//        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            Intent i = new Intent("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
//            for (Task task : dbHelper.getTasks()) {
//                locationManager.removeProximityAlert(
//                        PendingIntent.getBroadcast(context, task.getDb_id(), i, 0)
//                );
//
//            }
//        }
//    }

}
