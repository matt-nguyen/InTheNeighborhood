package com.nghianguyen.intheneighborhood.ui.task;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.nghianguyen.intheneighborhood.data.model.Task;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;

public class TaskModel {

    private final Task task;
    private final TaskDbManager manager;

    public TaskModel(TaskDbManager taskManager, int taskId){
        this.task = (taskId > -1) ? taskManager.getTask(taskId) : new Task();
        this.manager = taskManager;
    }

    public Task task(){
        return task;
    }

    public void saveTask(){
        String description = task.getDescription();
        boolean dontSave = description == null || TextUtils.isEmpty(description.trim());
        if(dontSave)
            return;

        if(task.getDb_id() > -1){
            manager.updateTask(task);
        }else{
            manager.addTask(task);
        }
    }

    public void setDescription(String description){
        task.setDescription(description);
    }

    public void setLocationName(String locationName){
        task.setLocName(locationName);
    }

    public void setLocation(Place place){
        task.setLocName(place.getName().toString());
        task.setLocAddress(place.getAddress().toString());
        task.setLocLatLng(place.getLatLng());
    }

    public void setLocMapImage(Bitmap locMapImage){
        task.setLocMapImage(locMapImage);
    }

    public void toggleDone(boolean isDone){
        task.setDone(isDone);
    }

    public void deleteTask(){
        manager.deleteTask(task);
    }
}
