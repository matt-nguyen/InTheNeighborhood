package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.google.android.gms.location.FusedLocationProviderClient;
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

    public FusedLocationProviderClient getFusedLocationProviderClient(){
        return fusedLocationProviderClient;
    }

    public void setProximityAlarmOn(boolean yes){
        proximityServiceAlarmManager.setAlarmOn(yes);
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



}
