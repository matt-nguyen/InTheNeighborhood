package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.List;

public class TaskListPresenter implements TaskListContract.Presenter{

    private TaskListContract.View view;
    private TaskListModel model;

    public TaskListPresenter(TaskListContract.View view, TaskListModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void loadTasks() {
        List<Task> tasks = model.getTasks();
        view.showTasks(tasks);
    }

    @Override
    public void finish() {
        view = null;
    }
}
