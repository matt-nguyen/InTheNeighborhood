package com.nghianguyen.intheneighborhood.ui.tasklist.adapter;

import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.nghianguyen.intheneighborhood.data.model.Task;
import com.nghianguyen.intheneighborhood.ui.tasklist.ContextMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskListItemPresenter {
    private List<Task> tasks;
    private ContextMenuRecyclerView recyclerView;

    public TaskListItemPresenter(List<Task> mTasks, ContextMenuRecyclerView recyclerView) {
        this.tasks = mTasks;
        this.recyclerView = recyclerView;
    }

    public void onBindViewAtPosition(TaskListItemView view, int position){
        view.setPresenter(this);

        Task task = tasks.get(position);

        view.displayTask(task);
        view.setDone(task.isDone());

        boolean isNearby = task.isNearby() && !task.isDone();
        view.showIsNearby(isNearby);
    }

    public void showContextMenu(View view){
        recyclerView.showContextMenuForChild(view);
    }

    public void updateNearbyTasks(Location location, float proximityDistance){
        List<Task> nearbyTasks = new ArrayList<>();

        LatLng locLatLng;
        Location taskLocation;
        for (Task task : tasks) {
            locLatLng = task.getLocLatLng();
            if(locLatLng != null){
                taskLocation = new Location("");
                taskLocation.setLatitude(locLatLng.latitude);
                taskLocation.setLongitude(locLatLng.longitude);

                if(!task.isDone() && proximityDistance >= (location.distanceTo(taskLocation) / 1609)){
                    task.setNearby(true);
                    nearbyTasks.add(task);
                }else{
                    task.setNearby(false);
                }
            }
        }

        // Shift nearby tasks to the top
        tasks.removeAll(nearbyTasks);
        tasks.addAll(0, nearbyTasks);
    }

    public void refresh(List<Task> updatedTasks) {
        tasks = updatedTasks;
    }

    public Task getTask(int position){
        return tasks.get(position);
    }

    public int getItemCount(){
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
