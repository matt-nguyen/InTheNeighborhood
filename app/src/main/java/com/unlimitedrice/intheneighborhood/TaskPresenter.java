package com.unlimitedrice.intheneighborhood;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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

            task.setLocName(placeName);
            task.setLocLatLng(latLng);

            map.clear();
            map.addMarker(new MarkerOptions().position(latLng).title(placeName));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    public void initMap(GoogleMap googleMap){
        map = googleMap;

        LatLng locLatLng = task.getLocLatLng();
        if(locLatLng != null){
            map.addMarker(new MarkerOptions().position(locLatLng).title(task.getLocName()));
            map.moveCamera(CameraUpdateFactory.newLatLng(locLatLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    public abstract void pickPlace();
}
