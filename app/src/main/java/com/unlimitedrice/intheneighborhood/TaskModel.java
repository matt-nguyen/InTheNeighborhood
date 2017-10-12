package com.unlimitedrice.intheneighborhood;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

/**
 * Created by unlim on 9/15/2017.
 */

public class TaskModel {

    private final Task task;
    private final TaskDbManager manager;

//    public TaskModel(TaskManager taskManager, UUID taskId){
//        this.task = taskManager.getTask(taskId);
//    }

    public TaskModel(TaskDbManager taskManager, int taskId){
        this.task = (taskId > -1) ? taskManager.getTask(taskId) : new Task();
        this.manager = taskManager;
    }

    public Task task(){
        return task;
    }

    public void saveTask(){
        if(TextUtils.isEmpty(task.getDescription()))
            return;

        if(task.getDb_id() > -1){
            Log.d("TESTING", "updating task");
            manager.updateTask(task);
        }else{
            Log.d("TESTING", "adding task");
            manager.addTask(task);
        }
    }

    public void setDescription(String description){
        task.setDescription(description);
    }

    public void setLocName(String locName){
        task.setLocName(locName);
    }

    public void setLocAddr(String locAddr){
        task.setLocAddress(locAddr);
    }

    public void setLocLatLng(LatLng locLatLng){
        task.setLocLatLng(locLatLng);
    }

    public void setLocMapImage(Bitmap locMapImage){
        task.setLocMapImage(locMapImage);
    }
}
