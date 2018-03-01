package com.nghianguyen.intheneighborhood.core;

import com.google.android.gms.maps.GoogleMap;

public interface MapsService {
    void pickPlace();

    void setMyLocationEnabled(GoogleMap googleMap, boolean isEnabled);
}
