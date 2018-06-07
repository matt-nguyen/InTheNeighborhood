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
import com.nghianguyen.intheneighborhood.ui.settings.SettingsActivity;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import java.util.List;

public class TaskListActivity extends GoogleApiConnectActivity implements TaskListContract.View{

    @BindView(R.id.task_recycler_view) public ContextMenuRecyclerView taskList;
    @BindView(R.id.fab) public FloatingActionButton fab;

    private TaskAdapter mAdapter;

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

        TaskListModel model = new TaskListModel(TaskDbManager.get(this), new FusedLocationProviderClient(this));

        presenter = new TaskListPresenter(this, model){
            @Override
            void startLocationUpdates(FusedLocationProviderClient fusedLocationProviderClient,
                                      LocationRequest locationRequest, LocationCallback locationCallback) {
                TaskListActivity.this.startLocationUpdates(fusedLocationProviderClient, locationRequest,
                        locationCallback);
            }
        };

        taskList.setLayoutManager(new LinearLayoutManager(this));

        registerForContextMenu(taskList);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(presenter != null){
                    presenter.refreshTasks();
                }
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
        presenter.onAttach();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        presenter.onDetach();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.finish();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            presenter.updateProximityAlerts(ProximityAlertManager.get(this));
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
        mAdapter = new TaskAdapter(this, tasks, taskList);

        taskList.setAdapter(mAdapter);
    }

    @Override
    public void updateLocation(LocationResult locationResult) {
        if(mAdapter != null){
            mAdapter.updateNearbyTasks(locationResult.getLastLocation());
        }
    }

    @Override
    public void updateAdapter(List<Task> tasks) {
        if(mAdapter != null){
            mAdapter.refresh(tasks);
        }
    }

    private void startLocationUpdates(FusedLocationProviderClient fusedLocationProviderClient,
                                      LocationRequest locationRequest, LocationCallback locationCallback){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

    }
}
