package com.unlimitedrice.intheneighborhood;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by unlim on 12/8/2016.
 */

public class Task {
    private UUID id;
    private String description;
    private String locName;
    private String locAddress;
    private LatLng locLatLng;

    public Task(){
        this(null, null);
    }

    public Task(String description, String locName){
        id = UUID.randomUUID();
        this.description = description;
        this.locName = locName;
    }

    public Task(JSONObject json) throws JSONException{
        Log.d("TESTING", "Building task - " + json.toString());
        id = UUID.fromString(json.getString("ID"));
        description = json.getString("DESCRIPTION");

        if(json.has("LOCNAME")) {
            locName = json.getString("LOCNAME");
        }
        // TODO: Test this logic when there's no lat/lng
        if(json.has("LAT") && json.has("LNG")) {
            double lat = json.getDouble("LAT");
            double lng = json.getDouble("LNG");
            locLatLng = new LatLng(lat, lng);
        }
    }

    public JSONObject toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", id.toString());
        jsonObject.put("DESCRIPTION", description);
        jsonObject.put("LOCNAME", locName);

        if(locLatLng != null) {
            jsonObject.put("LAT", locLatLng.latitude);
            jsonObject.put("LNG", locLatLng.longitude);
        }
        Log.d("TESTING", "task to json - " + jsonObject.toString());
        return jsonObject;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public String getLocAddress() {
        return locAddress;
    }

    public void setLocAddress(String locAddress) {
        this.locAddress = locAddress;
    }

    public LatLng getLocLatLng() {
        return locLatLng;
    }

    public void setLocLatLng(LatLng locLatLng) {
        this.locLatLng = locLatLng;
    }
}
