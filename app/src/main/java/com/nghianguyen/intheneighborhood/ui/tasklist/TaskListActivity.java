package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.alert.ProximityAlertManager;
import com.nghianguyen.intheneighborhood.data.model.Task;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.TaskOpenHelper;
import com.nghianguyen.intheneighborhood.map.GoogleApiConnectActivity;
import com.nghianguyen.intheneighborhood.settings.SettingsActivity;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends GoogleApiConnectActivity implements TaskListContract.View{

    @BindView(R.id.task_recycler_view) public ContextMenuRecyclerView taskList;
    @BindView(R.id.fab) public FloatingActionButton fab;

    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private BroadcastReceiver receiver;

    private TaskListContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TaskListActivity.this, TaskActivity.class));
            }
        });

        TaskListModel model = new TaskListModel(TaskDbManager.get(this));
        presenter = new TaskListPresenter(this, model);

        mTasks = getTasks();
        ContextMenuRecyclerView recyclerView = taskList;

        mAdapter = new TaskAdapter(this, mTasks, recyclerView);

        taskList.setAdapter(mAdapter);
        taskList.setLayoutManager(new LinearLayoutManager(this));

        registerForContextMenu(recyclerView);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTasks = getTasks();
                mAdapter.refresh(mTasks);
            }
        };

        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest()
                .setInterval(10 * 1000)
                .setFastestInterval(5 * 1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mAdapter.updateNearbyTasks(locationResult.getLastLocation());
            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(TaskOpenHelper.DB_UPDATED));
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        stopLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.finish();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_new_task:
                startActivity(new Intent(this, TaskActivity.class));
                return true;
            case R.id.action_clear_all_tasks:
                // Delete all tasks
                TaskDbManager.get(this).clearTasks();
                return true;
            case R.id.action_settings:
                startActivityForResult(
                        new Intent(this, SettingsActivity.class),
                        SettingsActivity.REQUEST_CODE
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int itemIndex = ((ContextMenuRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;

        Task task = ((TaskAdapter) taskList.getAdapter()).getTask(itemIndex);
        itemPos = itemIndex;
        if(task.isDone()) {
            menu.add(Menu.NONE, R.id.context_menu_mark_not_done, Menu.NONE,
                    R.string.context_menu_mark_not_done);
        }else{
            menu.add(Menu.NONE, R.id.context_menu_mark_done, Menu.NONE,
                    R.string.context_menu_mark_done);
        }

    }

    private int itemPos = -1;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.context_menu_mark_not_done:
                Toast.makeText(this, "marking not done - " + itemPos, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.context_menu_mark_done:
                Toast.makeText(this, "marking done - " + itemPos, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SettingsActivity.REQUEST_CODE){
            new ProximityAlertManager(this).updateAllProximityAlerts(getTasks());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("onRequestPermissionsRe", "ACCESS_FINE_LOCATION access granted");
            }
        }
    }

    @Override
    public void showTasks(List<Task> tasks) {

    }

    private void startLocationUpdates(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private ArrayList<Task> getTasks(){
        return TaskDbManager.get(this).getTasks();
    }
}
