package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.google.android.gms.location.LocationResult;
import com.nghianguyen.intheneighborhood.alert.ProximityAlertManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.List;

public interface TaskListContract {
    interface View{
        void showTasks(List<Task> tasks);
        void updateLocation(LocationResult locationResult);
        void updateAdapter(List<Task> tasks);
        void displayMessage(String message);
    }

    interface Presenter{
        void loadTasks();
        void refreshTasks();
        void setTaskDone(Task task, boolean isDone);
        void deleteTask(Task task);
        void updateProximityAlerts(ProximityAlertManager proximityAlertManager);
        void onAttach();
        void onDetach();
        void startLocationUpdates();
        void stopLocationUpdates();
        void finish();
    }
}
