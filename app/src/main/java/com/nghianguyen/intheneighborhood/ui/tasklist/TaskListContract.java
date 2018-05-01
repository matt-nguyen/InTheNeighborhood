package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.List;

public interface TaskListContract {
    interface View{
        void showTasks(List<Task> tasks);
    }

    interface Presenter{
        void loadTasks();
        void finish();
    }
}
