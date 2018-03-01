package com.nghianguyen.intheneighborhood.ui.task;

import com.google.android.gms.location.places.Place;

public interface TaskContact {

    interface View{
        void setPresenter(Presenter presenter);

        void displayDescription(String description);

        void showTaskDone(boolean isDone);

        void showLocationName(String locName);
    }

    interface Presenter{
        void pickPlace();

        void updatePlace(Place place);

        void setDescription(String description);

        void setDone(boolean isDone);
    }
}
