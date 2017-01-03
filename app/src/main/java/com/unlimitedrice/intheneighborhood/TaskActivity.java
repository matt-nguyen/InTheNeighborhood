package com.unlimitedrice.intheneighborhood;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.UUID;

public class TaskActivity extends GoogleApiConnectActivity implements OnMapReadyCallback {

    private static final float PROXIMITY_ALERT_RADIUS = 1609;

    public static final String EXTRA_TASK_ID =
            "com.unlimitedrice.intheneighborhood.task_id";
    public static final String EXTRA_TASK_POS =
            "com.unlimitedrice.intheneighborhood.task_pos";

    private GoogleMap mMap;
    private EditText mDescEditText;
    private Button mSelectPlaceButton;
    private Task mTask;

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Log.d("TaskActivity", "onCreate");
        Log.d("TaskActivity", "new log");
//        int position = getIntent().getIntExtra(EXTRA_TASK_POS, 0);
//        mTask = TaskManager.get(this).getTask(position);

        UUID taskId = (UUID)getIntent().getSerializableExtra(EXTRA_TASK_ID);
        Log.d("TaskActivity", "taskId - " + taskId.toString());
        mTask = TaskManager.get(this).getTask(taskId);

        mDescEditText = (EditText)findViewById(R.id.descriptionEditText);
        if(mTask.getDescription() != null){
            mDescEditText.setText(mTask.getDescription());
        }

        mDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mTask.setDescription((editable != null) ? editable.toString() : null);
            }
        });

        mSelectPlaceButton = (Button)findViewById(R.id.selectPlaceButton);
        mSelectPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(TaskActivity.this);

                    startActivityForResult(intent, 0);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        if(mTask.getLocName() != null){
            mSelectPlaceButton.setText(mTask.getLocName());
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // TODO: Add grocery list to task obj and activity
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("onMapReady", "on map ready");
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Center map onto task location or on user's location(if no task location)
        LatLng latLng = mTask.getLocLatLng();
        if(latLng != null){
            mMap.addMarker(new MarkerOptions().position(latLng).title(mTask.getLocName()));

            // TODO: Possibly modify zoom to show both the task location and the current location?
        }else{

            Location currentLocation = GoogleServiceManager.get(this).getLastLocation();
            if(currentLocation != null) {
                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }else{
                Log.d("onMapReady", "did not get location");
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "done picking");

        if(resultCode == Activity.RESULT_OK){
            Place place = PlacePicker.getPlace(this, data);

            if(place != null) {

                // Update the task and map with the new place
                String placeName = place.getName().toString();
                LatLng latLng = place.getLatLng();

                mSelectPlaceButton.setText(placeName);

                mTask.setLocName(placeName);
                mTask.setLocLatLng(latLng);

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));


                // Add/update the proximity alert
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

//                    if(mLocationManager == null) {
//                        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    }
//
//
//                    Intent intent = new Intent(this, TaskActivity.class);
//                    intent.setAction("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
//                    intent.putExtra(EXTRA_TASK_ID, mTask.getId());
//
//                    PendingIntent pi = PendingIntent.getBroadcast(this,
//                            Integer.parseInt(mTask.getId().toString()), // Id to add/remove in locationmanager
//                            intent,
//                            0);
//
//                    mLocationManager.removeProximityAlert(pi);
//
//                    Log.d("onActivityResult", "Adding proximity alert");
//                    mLocationManager.addProximityAlert(latLng.latitude, latLng.longitude,
//                            PROXIMITY_ALERT_RADIUS,
//                            -1,
//                            pi);

                }
            }
        }
    }

}
