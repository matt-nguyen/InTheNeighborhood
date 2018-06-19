package com.nghianguyen.intheneighborhood.ui.tasklist;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nghianguyen.intheneighborhood.alert.ProximityAlertManager;
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.util.List;

public abstract class TaskListPresenter implements TaskListContract.Presenter{

    private TaskListContract.View view;
    private TaskListModel model;

    private boolean hasLoaded = false;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public TaskListPresenter(final TaskListContract.View view, TaskListModel model) {
        this.view = view;
        this.model = model;

        locationRequest = new LocationRequest()
                .setInterval(10 * 1000)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

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
    public void startLocationUpdates() {
        startLocationUpdates(model.getFusedLocationProviderClient(), locationRequest, locationCallback);
    }

    @Override
    public void onDetach() {
        stopLocationUpdates();
    }

    @Override
    public void stopLocationUpdates() {
        model.getFusedLocationProviderClient().removeLocationUpdates(locationCallback);
    }

    @Override
    public void updateProximityAlerts(ProximityAlertManager proximityAlertManager) {
        // TODO: Should have model include a method to do this so we aren't just passing a proximityalertmanager around like this
        proximityAlertManager.updateAllProximityAlerts(model.getTasks());
    }

    @Override
    public void finish() {
        view = null;
    }

    abstract void startLocationUpdates(FusedLocationProviderClient fusedLocationProviderClient,
                                            LocationRequest locationRequest,
                                            LocationCallback locationCallback);
}
