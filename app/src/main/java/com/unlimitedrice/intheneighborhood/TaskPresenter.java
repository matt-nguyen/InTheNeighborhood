package com.unlimitedrice.intheneighborhood;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by unlim on 9/15/2017.
 */

public abstract class TaskPresenter {

    private final Task task;
    private final TaskView view;
    private final TaskModel model;
    private GoogleMap map;

    public TaskPresenter(TaskView view, final TaskModel model){
        this.view = view;
        this.task = model.task();
        this.model = model;

        view.descriptionEditText.setText(task.getDescription());

        if(task.getLocName() != null){
            view.selectPlaceButton.setText(task.getLocName());
        }

        view.descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                model.setDescription((editable != null) ? editable.toString() : null);
            }
        });

        view.selectPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPlace();
            }
        });
    }

    public void updatePlace(Place place){
        if(place != null){
            String placeName = place.getName().toString();
            LatLng latLng = place.getLatLng();

            view.selectPlaceButton.setText(placeName);
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng).title(placeName));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            model.setLocName(placeName);
            model.setLocAddr(place.getAddress().toString());
            model.setLocLatLng(latLng);
            saveSnapshot();

        }
    }


    public void initMap(GoogleMap googleMap, Location currentLocation){
        this.map = googleMap;

        LatLng locLatLng = task.getLocLatLng();
        if(locLatLng != null){
            map.addMarker(new MarkerOptions().position(locLatLng).title(task.getLocName()));

        }else{
            if(currentLocation != null){
                locLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
        }

        if(locLatLng != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(locLatLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    public void finish(){
        model.saveTask();
    }

    private void saveSnapshot(){
        setMyLocationEnabled(false); // hide the current location indicator for the snapshot
        map.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                if(bitmap != null) {
                    // Determine start of Y for cropping
                    int height = bitmap.getHeight();
                    int newHeight = (int) (height * 0.3);
                    int startY = (height - newHeight) / 2;

                    model.setLocMapImage(
                            Bitmap.createBitmap(bitmap, 0, startY, bitmap.getWidth(), newHeight)
                    );

                    setMyLocationEnabled(true); // display the current indicator after snapshot
                }
            }
        });

    }


    private void setMyLocationEnabled(boolean enabled){
        if(ContextCompat.checkSelfPermission(view.descriptionEditText.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(enabled);
        }
    }

    public abstract void pickPlace();
}
