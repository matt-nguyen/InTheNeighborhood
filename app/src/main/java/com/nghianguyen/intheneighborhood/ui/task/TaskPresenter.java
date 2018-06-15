package com.nghianguyen.intheneighborhood.ui.task;

import android.graphics.Bitmap;
import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nghianguyen.intheneighborhood.core.MapsService;
import com.nghianguyen.intheneighborhood.data.model.Task;

public abstract class TaskPresenter implements TaskContract.Presenter{

    private Task task;
    private TaskContract.View view;
    private TaskModel model;
    private GoogleMap map;
    private MapsService mapsService;

    private boolean taskDeleted = false;

    public TaskPresenter(TaskContract.View view, TaskModel model,
                         MapsService mapsService){
        this.view = view;
        this.task = model.task();
        this.model = model;
        this.mapsService = mapsService;

        view.setPresenter(this);

        view.displayDescription(task.getDescription());
        view.showLocationName(task.getLocName());
        view.showLocationAddress(task.getLocAddress());
        view.showTaskDone(task.isDone());
    }

    @Override
    public void setDescription(String description) {
        if(!TextUtils.isEmpty(description)) {
            model.setDescription(description);
        }
    }

    @Override
    public void setLocationName(String locationName) {
        if(!TextUtils.isEmpty(locationName)){
            model.setLocationName(locationName);
        }
    }

    @Override
    public void markDoneStatus(boolean isDone) {
        model.toggleDone(isDone);
    }


    @Override
    public void initializeMap(GoogleMap googleMap, Location currentLocation){
        this.map = googleMap;

        float zoom = 13;
        LatLng locLatLng = task.getLocLatLng();
        if(locLatLng != null){
            map.addMarker(new MarkerOptions().position(locLatLng).title(task.getLocName()));
            zoom = 16;
        }else{
            if(currentLocation != null){
                locLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
        }

        if(locLatLng != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(locLatLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }

    }

    @Override
    public void pickPlace() {
        mapsService.pickPlace();
    }

    @Override
    public void onPlaceUpdated(Place place){
        if(place != null){
            String placeName = place.getName().toString();
            LatLng latLng = place.getLatLng();

            if(!view.isLocationNameEntered()){
                view.showLocationName(placeName);
            }
            view.showLocationAddress(place.getAddress().toString());

            model.setLocation(place);

            updateMapVisual(latLng, placeName);

            startSavingSnapshot();
        }
    }

    private void updateMapVisual(LatLng latLng, String placeName){
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng).title(placeName));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private void startSavingSnapshot(){
        mapsService.setMyLocationEnabled(map, false);
        beginSavingSnapshot();
    }


    @Override
    public void saveSnapshot() {
        map.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if(bitmap != null) {
                    model.setLocMapImage(processBitmap(bitmap));
                }

                mapsService.setMyLocationEnabled(map, true);
            }
        });
    }

    private Bitmap processBitmap(Bitmap bitmap){
        int height = bitmap.getHeight();

        int newHeight = (int) (height * 0.4);
        int startY = (height - newHeight) / 2;

        return Bitmap.createBitmap(bitmap, 0, startY, bitmap.getWidth(), newHeight);
    }

    @Override
    public void deleteTask() {
        model.deleteTask();
        taskDeleted = true;
        exitScreen();
    }


    @Override
    public void finish(){
        if(!taskDeleted) {
            model.saveTask();
        }
        view = null;
        mapsService = null;
    }

    abstract void beginSavingSnapshot();

    abstract void exitScreen();
}
