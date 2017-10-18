package com.nghianguyen.intheneighborhood.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Task {
    private UUID id;
    private int db_id = -1;
    private String description;
    private String locName;
    private LatLng locLatLng;
<<<<<<< HEAD:app/src/main/java/com/nghianguyen/intheneighborhood/data/Task.java
    private Bitmap locMapImage;
    private boolean isNearby = false;
=======
    private boolean isDone;
    private boolean isNearby;
    private long createdDate;
>>>>>>> origin/master:app/src/main/java/com/unlimitedrice/intheneighborhood/Task.java

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

    public Task(int db_id, String description, String locName, String locAddress, double lat, double lng, Bitmap locMapImage){
        this.db_id = db_id;
        this.description = description;
        this.locName = locName;
        this.locAddress = locAddress;
        this.locMapImage = locMapImage;

        if(locAddress != null && locAddress.length() > 0){
            this.locLatLng = new LatLng(lat, lng);
        }
    }

    public Task(JSONObject json) throws JSONException{
        Log.d("TESTING", "Building task - " + json.toString());
        id = UUID.fromString(json.getString("ID"));
        description = json.getString("DESCRIPTION");
<<<<<<< HEAD:app/src/main/java/com/nghianguyen/intheneighborhood/data/Task.java
=======
        locName = json.getString("LOCNAME");
//        createdDate = json.getLong("CREATEDDATE");
        isDone = json.getBoolean("ISDONE");
>>>>>>> origin/master:app/src/main/java/com/unlimitedrice/intheneighborhood/Task.java

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
//        jsonObject.put("CREATEDDATE", createdDate);
        jsonObject.put("ISDONE", isDone);

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

    public LatLng getLocLatLng() {
        return locLatLng;
    }

    public void setLocLatLng(LatLng locLatLng) {
        this.locLatLng = locLatLng;
    }

<<<<<<< HEAD:app/src/main/java/com/nghianguyen/intheneighborhood/data/Task.java
    public Bitmap getLocMapImage() {
        return locMapImage;
    }

    public void setLocMapImage(Bitmap locMapImage) {
        this.locMapImage = locMapImage;
=======
    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
>>>>>>> origin/master:app/src/main/java/com/unlimitedrice/intheneighborhood/Task.java
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }
<<<<<<< HEAD:app/src/main/java/com/nghianguyen/intheneighborhood/data/Task.java

    @Override
    public String toString() {
        String latLngPortion = "";
        if(locLatLng != null){
            latLngPortion = " , lat - " + locLatLng.latitude + ", lng - " + locLatLng.longitude;
        }

        return "Task db_id - " + db_id + ", desc - " + description + ", locname - " + locName +
                ", locaddr - "+ locAddress + latLngPortion;
    }
=======
>>>>>>> origin/master:app/src/main/java/com/unlimitedrice/intheneighborhood/Task.java
}
