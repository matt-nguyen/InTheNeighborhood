package com.nghianguyen.intheneighborhood.ui.tasklist.adapter;

import com.nghianguyen.intheneighborhood.data.model.Task;

public interface TaskListItemView {
    void setPresenter(TaskListItemPresenter presenter);
    void displayTask(Task task);
    void setDone(boolean isDone);
    void showIsNearby(boolean isNearby);

}
