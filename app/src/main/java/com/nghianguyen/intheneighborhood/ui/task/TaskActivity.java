package com.nghianguyen.intheneighborhood.ui.task;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.map.GoogleApiConnectActivity;
import com.nghianguyen.intheneighborhood.map.GoogleServiceManager;

public class TaskActivity extends GoogleApiConnectActivity implements OnMapReadyCallback {

    public static final String EXTRA_TASK_ID =
            "com.nghianguyen.intheneighborhood.task_id";

    private GoogleMap mMap;

    private TaskView taskView;
    private TaskPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskView = new TaskView(this);

        Intent intent = getIntent();
        int taskId = -1;
        if(intent != null){
            taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
        }

        final TaskModel model = new TaskModel(TaskDbManager.get(this), taskId);
        presenter = new TaskPresenter(taskView, model, this) {
            @Override
            public void pickPlace() {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

                try {
                    Intent intent = intentBuilder.build(TaskActivity.this);
                    startActivityForResult(intent, 0);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onDestroy() {
        presenter.finish();
        super.onDestroy();
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

        Location currentLocation = GoogleServiceManager.get(this).getLastLocation();
        presenter.initMap(mMap, currentLocation);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "done picking");

        if(resultCode == Activity.RESULT_OK){
            Place place = PlacePicker.getPlace(this, data);
            presenter.updatePlace(place);
        }
    }

}
