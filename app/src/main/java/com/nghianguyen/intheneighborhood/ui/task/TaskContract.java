package com.nghianguyen.intheneighborhood.ui.task;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;

public interface TaskContract {

    interface View{
        void setPresenter(Presenter presenter);

        void displayDescription(String description);

        void showTaskDone(boolean isDone);

        void showLocationName(String locName);

        boolean isLocationNameEntered();

        void showLocationAddress(String address);
    }

    interface Presenter{
        void initializeMap(GoogleMap googleMap, Location currentLocation);

        void pickPlace();

        void onPlaceUpdated(Place place);

        void setDescription(String description);

        void setLocationName(String locationName);

        void markDoneStatus(boolean isDone);

        void saveSnapshot();

        void deleteTask();

        void finish();
    }
}
