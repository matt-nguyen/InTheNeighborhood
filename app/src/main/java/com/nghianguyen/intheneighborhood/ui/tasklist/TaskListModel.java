package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.nghianguyen.intheneighborhood.core.ProximityServiceAlarmManager;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;

public class TaskListModel {
    private TaskDbManager taskManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ProximityServiceAlarmManager proximityServiceAlarmManager;

    public TaskListModel(TaskDbManager taskManager,
                         FusedLocationProviderClient fusedLocationProviderClient,
                         ProximityServiceAlarmManager proximityServiceAlarmManager) {
        this.taskManager = taskManager;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.proximityServiceAlarmManager = proximityServiceAlarmManager;
    }


    public ArrayList<Task> getTasks(){
        return taskManager.getTasks();
    }

    public void updateTask(Task task){
        taskManager.updateTask(task);
    }

    public void deleteTask(Task task){
        taskManager.deleteTask(task);
    }

    public void startLocationUpdates(LocationRequest locationRequest, LocationCallback locationCallback){
        Context context = fusedLocationProviderClient.getApplicationContext();
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public void stopLocationUpdates(LocationCallback locationCallback){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void setProximityAlarmOn(boolean yes){
        proximityServiceAlarmManager.setAlarmOn(yes);
    }

}
