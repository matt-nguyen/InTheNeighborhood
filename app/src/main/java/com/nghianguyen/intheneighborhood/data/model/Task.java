package com.nghianguyen.intheneighborhood.data.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Task {
    private UUID id;
    private int db_id = -1;
    private String description;
    private String locName;
    private String locAddress;
    private LatLng locLatLng;
    private Bitmap locMapImage;
    private boolean isNearby = false;
    private boolean isDone = false;

    public Task(){
        this(null, null);
    }

    public Task(String description, String locName){
        id = UUID.randomUUID();
        this.description = description;
        this.locName = locName;
    }

    public Task(int db_id, String description, String locName, String locAddress, double lat,
                double lng, Bitmap locMapImage, boolean isDone){
        this.db_id = db_id;
        this.description = description;
        this.locName = locName;
        this.locAddress = locAddress;
        this.locMapImage = locMapImage;
        this.isDone = isDone;

        if(locAddress != null && locAddress.length() > 0){
            this.locLatLng = new LatLng(lat, lng);
        }
    }

    public Task(JSONObject json) throws JSONException{
        id = UUID.fromString(json.getString("ID"));
        description = json.getString("DESCRIPTION");

        if(json.has("LOCNAME")) {
            locName = json.getString("LOCNAME");
        }

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

        return jsonObject;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
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

    public Bitmap getLocMapImage() {
        return locMapImage;
    }

    public void setLocMapImage(Bitmap locMapImage) {
        this.locMapImage = locMapImage;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        String latLngPortion = "";
        if(locLatLng != null){
            latLngPortion = " , lat - " + locLatLng.latitude + ", lng - " + locLatLng.longitude;
        }

        return "Task db_id - " + db_id + ", desc - " + description + ", locname - " + locName +
                ", locaddr - "+ locAddress + latLngPortion;
    }
}