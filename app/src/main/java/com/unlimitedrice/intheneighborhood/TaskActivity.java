package com.unlimitedrice.intheneighborhood;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    public static final int REQUEST_CODE = 12;

    public static final String EXTRA_TASK_ID =
            "com.unlimitedrice.intheneighborhood.task_id";
    public static final String EXTRA_TASK_POS =
            "com.unlimitedrice.intheneighborhood.task_pos";

    private GoogleMap mMap;
    private EditText mDescEditText;
    private Button mSelectPlaceButton;
    private Task mTask;

    private LocationManager mLocationManager;

    private TaskView taskView;
    private TaskPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskView = new TaskView(this);
//        int position = getIntent().getIntExtra(EXTRA_TASK_POS, 0);
//        mTask = TaskManager.get(this).getTask(position);

        Intent intent = getIntent();
        int taskId = -1;
        if(intent != null){
            taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
        }

//        UUID taskId = (UUID) intent.getSerializableExtra(EXTRA_TASK_ID);
//        Log.d("TaskActivity", "taskId - " + taskId.toString());

//        final TaskModel model = new TaskModel(TaskManager.get(this), taskId);
        final TaskModel model = new TaskModel(TaskDbManager.get(this), taskId);
        presenter = new TaskPresenter(taskView, model) {
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

//    @Override
//    protected void onDestroy() {
//        if(!TextUtils.isEmpty(taskView.descriptionEditText.getText())) {
//            Log.d("TESTING", "about to save task");
//            presenter.finish();
//            setResult(RESULT_OK);
//        }else{
//            Log.d("TESTING", "not saving task");
//        }
//        super.onDestroy();
//    }

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
//            if(place != null) {
//
//                // Add/update the proximity alert
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                        == PackageManager.PERMISSION_GRANTED) {
//
//                    if(mLocationManager == null) {
//                        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    }
//
//
//                    Intent intent = new Intent(this, TaskActivity.class);
//                    intent.setAction("com.unlimitedrice.intheneighborhood.PROXIMITY_ALERT");
//                    intent.putExtra(EXTRA_TASK_ID, mTask.getDb_id());
//
//                    PendingIntent pi = PendingIntent.getBroadcast(this,
//                            Integer.parseInt(mTask.getId().toString()), // Id to add/remove in locationmanager
//                            intent,
//                            0);
//
//                    mLocationManager.removeProximityAlert(pi);
//
//                    LatLng latLng = place.getLatLng();
//
//                    Log.d("onActivityResult", "Adding proximity alert");
//                    mLocationManager.addProximityAlert(latLng.latitude, latLng.longitude,
//                            PROXIMITY_ALERT_RADIUS,
//                            -1,
//                            pi);
//
//                }
//            }
        }
    }

}
