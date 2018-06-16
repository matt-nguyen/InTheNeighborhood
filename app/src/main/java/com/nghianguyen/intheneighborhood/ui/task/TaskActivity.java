package com.nghianguyen.intheneighborhood.ui.task;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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
import com.nghianguyen.intheneighborhood.core.SimpleTextWatcher;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.map.GoogleApiConnectActivity;
import com.nghianguyen.intheneighborhood.map.GoogleServiceManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskActivity extends GoogleApiConnectActivity implements OnMapReadyCallback,
        MapsService, TaskContract.View, DontCreateDialogFragment.Listener,
        DiscardDialogFragment.Listener{

    public static final String EXTRA_TASK_ID = "task_id";

    @BindView(R.id.descriptionEditText) public EditText descriptionEditText;
    @BindView(R.id.selectPlaceButton) public TextView selectPlaceButton;
    @BindView(R.id.isDoneCheckBox) public CheckBox isDoneCheckbox;
    @BindView(R.id.location_name) public EditText locationName;
    @BindView(R.id.location_address) public TextView addressText;
    @BindView(R.id.delete_button) public View deleteButton;
    @BindView(R.id.remove_place_button) public View removePlaceButton;

    private TaskContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setTitle("Task");
        }

        Intent intent = getIntent();
        int taskId = -1;
        if(intent != null){
            taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);

            if(taskId == -1) {
                deleteButton.setVisibility(View.GONE);
            }
        }

        setup(taskId);
    }

    @Override
    public void onBackPressed() {
        if(presenter.isReadyToExit()) {
            super.onBackPressed();
        }else{
            showAlert();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(presenter.isReadyToExit()){
                    onBackPressed();
                }else{
                    showAlert();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.finish();
        super.onDestroy();
    }

    private void setup(int taskId){
        TaskModel model = new TaskModel(TaskDbManager.get(this), taskId);
        presenter = new TaskPresenter(this, model, this){
            @Override
            void beginSavingSnapshot() {
                TaskActivity.this.beginSavingSnapshot();
            }

            @Override
            void onTaskDeleted() {
                TaskActivity.this.setResult(RESULT_OK);
                TaskActivity.this.finish();
            }
        };

        setupViewEvents();

        startMap();
    }

    private void setupViewEvents(){
        descriptionEditText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                presenter.setDescription(s.toString());
            }
        });

        locationName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                presenter.setLocationName(s.toString());
            }
        });

        isDoneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                presenter.markDoneStatus(b);
            }
        });

        selectPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.pickPlace();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDeleteDialog();
            }
        });

        removePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.removePlace();
            }
        });
    }

    private void startMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            presenter.onPlaceUpdated(place);
            removePlaceButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayDescription(String description) {
        descriptionEditText.setText(description);
    }

    @Override
    public void showTaskDone(boolean isDone) {
        isDoneCheckbox.setChecked(isDone);
    }

    @Override
    public void showLocationName(String locName) {
        if(!TextUtils.isEmpty(locName)) {
            selectPlaceButton.setText(R.string.button_update_place);
            removePlaceButton.setVisibility(View.VISIBLE);
        }else{
            selectPlaceButton.setText(R.string.button_select_place);
        }

        locationName.setText(locName);
    }

    @Override
    public boolean isLocationNameEntered() {
        return !TextUtils.isEmpty(locationName.getText());
    }

    @Override
    public void showLocationAddress(String address) {
        if(!TextUtils.isEmpty(address)) {
            addressText.setText(address);
        }else{
            addressText.setText(R.string.task_location_address_label);
        }
    }

    @Override
    public void clearLocation() {
        showLocationAddress(null);
        showLocationName(null);

        selectPlaceButton.setText(R.string.button_select_place);
        removePlaceButton.setVisibility(View.GONE);
    }

    @Override
    public void deleteTaskConfirmed() {
        presenter.deleteTask();
    }

    private void openConfirmDeleteDialog(){
        new ConfirmDeleteDialogFragment().show(getSupportFragmentManager(), "dialog");
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

    private void showAlert(){
        if(presenter.isNewTask()) {
            new DontCreateDialogFragment().show(getSupportFragmentManager(), "dontcreate");
        }else{
            new DiscardDialogFragment().show(getSupportFragmentManager(), "discard");
        }
    }

    @Override
    public void exitWithoutSaving() {
        finish();
    }

    @Override
    public void discardAndExit() {
        presenter.deleteTask();
    }
}
