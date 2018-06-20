package com.nghianguyen.intheneighborhood.ui.task;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;

public interface TaskContract {

    interface View{
        void displayDescription(String description);

        void showTaskDone(boolean isDone);

        void showLocationName(String locName);

        boolean isLocationNameEntered();

        void showLocationAddress(String address);

        void clearLocation();

        void deleteTaskConfirmed();
    }

    interface Presenter{
        void initializeMap(GoogleMap googleMap, Location currentLocation);

        void pickPlace();

        void onPlaceUpdated(Place place);

        void setDescription(String description);

        void setLocationName(String locationName);

        void markDoneStatus(boolean isDone);

        void removePlace();

        void saveSnapshot();

        void deleteTask();

        boolean isSaveable();

        boolean isNewTask();

        void saveTask();

        void finish();
    }
}
