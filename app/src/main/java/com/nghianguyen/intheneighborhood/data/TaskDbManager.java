package com.nghianguyen.intheneighborhood.data;

import android.content.Context;

import com.nghianguyen.intheneighborhood.alert.ProximityAlertManager;

import java.util.ArrayList;

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
//        removeAllProximityAlerts();
        proximityAlertManager.removeAllProximityAlerts(dbHelper.getTasks());
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
//    private void removeAllProximityAlerts(){
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
