package com.nghianguyen.intheneighborhood.ui.task;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.core.MapsService;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.map.GoogleApiConnectActivity;
import com.nghianguyen.intheneighborhood.map.GoogleServiceManager;

public class TaskActivity extends GoogleApiConnectActivity implements OnMapReadyCallback, MapsService {

    public static final String EXTRA_TASK_ID =
            "com.nghianguyen.intheneighborhood.task_id";

    private TaskContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        int taskId = -1;
        if(intent != null){
            taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
        }

        TaskContract.View taskView = new TaskView(this);

        TaskModel model = new TaskModel(TaskDbManager.get(this), taskId);

        presenter = new TaskPresenter(taskView, model, this){
            @Override
            void beginSavingSnapshot() {
                TaskActivity.this.beginSavingSnapshot();
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onDestroy() {
        presenter.finish();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        setMyLocationEnabled(googleMap, true);

        Location currentLocation = GoogleServiceManager.get(this).getLastLocation();
        presenter.initializeMap(googleMap, currentLocation);

    }

    @Override
    public void pickPlace() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(TaskActivity.this);
            startActivityForResult(intent, 0);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this,
                    "You need Google Play Services installed/updated in order to use this feature",
                    Toast.LENGTH_LONG).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Services unavailable to pick location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setMyLocationEnabled(GoogleMap googleMap, boolean isEnabled) {
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(isEnabled);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Place place = PlacePicker.getPlace(this, data);
            presenter.updatePlace(place);
        }
    }

    private void beginSavingSnapshot(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    TaskActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            presenter.saveSnapshot();
                        }
                    });
                }
            }
        }).start();
    }

}
