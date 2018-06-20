package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.List;

public class TaskListPresenter implements TaskListContract.Presenter{

    private TaskListContract.View view;
    private TaskListModel model;

    private boolean hasLoaded = false;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public TaskListPresenter(final TaskListContract.View view, TaskListModel model) {
        this.view = view;
        this.model = model;

        locationRequest = new LocationRequest()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                view.updateLocation(locationResult);
            }
        };
    }

    @Override
    public void onAttach() {
        if(!hasLoaded) {
            loadTasks();
        }

        startLocationUpdates();
    }

    @Override
    public void loadTasks() {
        List<Task> tasks = model.getTasks();
        view.showTasks(tasks);

        hasLoaded = true;
    }

    @Override
    public void refreshTasks() {
        view.updateAdapter(model.getTasks());
    }

    @Override
    public void setTaskDone(Task task, boolean isDone) {
        task.setDone(isDone);
        model.updateTask(task);
        refreshTasks();
    }

    @Override
    public void deleteTask(Task task) {
        model.deleteTask(task);
        refreshTasks();

        view.displayMessage("Removed - " + task.getDescription());
    }

    @Override
    public void setProximityAlertsOn(boolean yes) {
        model.setProximityAlarmOn(yes);
    }

    @Override
    public void startLocationUpdates() {
        model.startLocationUpdates(locationRequest, locationCallback);
    }

    @Override
    public void onDetach() {
        stopLocationUpdates();
    }

    @Override
    public void stopLocationUpdates() {
        model.stopLocationUpdates(locationCallback);
    }

    @Override
    public void finish() {
        view = null;
    }

}
