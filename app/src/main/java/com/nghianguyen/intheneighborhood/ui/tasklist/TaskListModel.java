package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.List;

public class TaskListModel {
    private TaskDbManager taskManager;

    public TaskListModel(TaskDbManager taskManager) {
        this.taskManager = taskManager;
    }

    public List<Task> getTasks(){
        return taskManager.getTasks();
    }
}
