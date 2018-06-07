package com.nghianguyen.intheneighborhood.data;

import android.content.Context;

import com.nghianguyen.intheneighborhood.alert.ProximityAlertManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.ArrayList;

public class TaskDbManager {
    private static TaskDbManager taskDbManager;
    private TaskOpenHelper dbHelper;

    private ProximityAlertManager proximityAlertManager;

    private TaskDbManager(Context context){
        this.dbHelper = new TaskOpenHelper(context);
        this.proximityAlertManager = ProximityAlertManager.get(context);
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
        proximityAlertManager.addProximityAlert(t, id);
    }

    public void updateTask(Task t){
        long id = dbHelper.updateTask(t);
        proximityAlertManager.addProximityAlert(t, id);
    }

    public void clearTasks(){
        dbHelper.clearTasks();
        proximityAlertManager.removeAllProximityAlerts(dbHelper.getTasks());
    }


}
