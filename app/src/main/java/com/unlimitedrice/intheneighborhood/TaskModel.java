package com.unlimitedrice.intheneighborhood;

import java.util.UUID;

/**
 * Created by unlim on 9/15/2017.
 */

public class TaskModel {

    private final Task task;

    public TaskModel(TaskManager taskManager, UUID taskId){
        this.task = taskManager.getTask(taskId);
    }

    public Task task(){
        return task;
    }

    public void setDescription(String description){
        task.setDescription(description);
    }
}
