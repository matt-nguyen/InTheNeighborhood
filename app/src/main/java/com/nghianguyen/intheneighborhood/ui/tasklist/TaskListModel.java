package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListModel {
    private TaskDbManager taskManager;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public TaskListModel(TaskDbManager taskManager,
                         FusedLocationProviderClient fusedLocationProviderClient) {
        this.taskManager = taskManager;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public ArrayList<Task> getTasks(){
        return taskManager.getTasks();
    }

    public FusedLocationProviderClient getFusedLocationProviderClient(){
        return fusedLocationProviderClient;
    }
}
