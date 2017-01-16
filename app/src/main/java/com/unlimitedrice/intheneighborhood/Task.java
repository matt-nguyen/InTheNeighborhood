package com.unlimitedrice.intheneighborhood;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by unlim on 12/8/2016.
 */

public class Task {
    private UUID id;
    private String description;
    private String locName;
    private LatLng locLatLng;
    private boolean isDone;
    private boolean isNearby;
    private long createdDate;

    public Task(){
        this(null, null);
    }

    public Task(String description, String locName){
        this.description = description;
        this.locName = locName;

        id = UUID.randomUUID();
        isDone = false;

        createdDate = new Date().getTime();
    }

    public Task(JSONObject json) throws JSONException{
        id = UUID.fromString(json.getString("ID"));
        description = json.getString("DESCRIPTION");
        locName = json.getString("LOCNAME");
//        createdDate = json.getLong("CREATEDDATE");
        isDone = json.getBoolean("ISDONE");

        // TODO: Test this logic when there's no lat/lng
        double lat = json.getDouble("LAT");
        double lng = json.getDouble("LNG");
        locLatLng = new LatLng(lat, lng);
    }

    public JSONObject toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", id.toString());
        jsonObject.put("DESCRIPTION", description);
        jsonObject.put("LOCNAME", locName);
//        jsonObject.put("CREATEDDATE", createdDate);
        jsonObject.put("ISDONE", isDone);

        if(locLatLng != null) {
            jsonObject.put("LAT", locLatLng.latitude);
            jsonObject.put("LNG", locLatLng.longitude);
        }
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

    public LatLng getLocLatLng() {
        return locLatLng;
    }

    public void setLocLatLng(LatLng locLatLng) {
        this.locLatLng = locLatLng;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }
}
