package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.core.ProximityServiceAlarmManager;
import com.nghianguyen.intheneighborhood.data.model.Task;
import com.nghianguyen.intheneighborhood.data.TaskDbManager;
import com.nghianguyen.intheneighborhood.data.TaskOpenHelper;
import com.nghianguyen.intheneighborhood.map.GoogleApiConnectActivity;
import com.nghianguyen.intheneighborhood.ui.settings.SettingsActivity;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;
import com.nghianguyen.intheneighborhood.ui.tasklist.adapter.TaskListItemPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import java.util.List;

public class TaskListActivity extends GoogleApiConnectActivity implements TaskListContract.View,
        DeviceLocationPermissionsDialogFragment.Listener{
    public static final int REQUEST_CODE_TASK_DELETED = 100;

    @BindView(R.id.task_recycler_view) public ContextMenuRecyclerView taskList;
    @BindView(R.id.fab) public FloatingActionButton fab;

    private TaskAdapter mAdapter;
    private TaskListContract.Presenter presenter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(presenter != null){
                presenter.refreshTasks();
            }
        }
    };

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

        TaskListModel model = new TaskListModel(TaskDbManager.get(this),
                new FusedLocationProviderClient(this),
                ProximityServiceAlarmManager.get(this));

        presenter = new TaskListPresenter(this, model);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean needToCheck = sharedPrefs.getBoolean("need_permissions_check", true);
        if(needToCheck) {
            checkPermissions();
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

        getMenuInflater().inflate(R.menu.menu_context_task, menu);

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
        Task task = mAdapter.getTask(itemPos);

        switch (item.getItemId()){
            case R.id.edit_task:
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getDb_id());

                startActivityForResult(intent, REQUEST_CODE_TASK_DELETED);
                return true;
            case R.id.delete_task:
                presenter.deleteTask(task);
                return true;
            case R.id.context_menu_mark_not_done:
                presenter.setTaskDone(task, false);
                return true;
            case R.id.context_menu_mark_done:
                presenter.setTaskDone(task, true);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SettingsActivity.REQUEST_CODE){
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean gpsAlertsOn = sharedPrefs.getBoolean("pref_gps", false);

            presenter.setProximityAlertsOn(gpsAlertsOn);

        }else if(requestCode == REQUEST_CODE_TASK_DELETED){
//            if(resultCode == RESULT_OK) {
                presenter.refreshTasks();
//            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.setProximityAlertsOn(true);
            }else{
                displayPermissionsWarning();
            }
        }
    }

    @Override
    public void showTasks(List<Task> tasks) {
        TaskListItemPresenter taskListItemPresenter = new TaskListItemPresenter(tasks, taskList);
        mAdapter = new TaskAdapter(this, taskListItemPresenter){
            @Override
            void startActivityForResult(Intent intent) {
                TaskListActivity.this.startActivityForResult(intent, REQUEST_CODE_TASK_DELETED);
            }
        };

        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setAdapter(mAdapter);
        registerForContextMenu(taskList);
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

    @Override
    public void displayMessage(String message) {
        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void displayPermissionsWarning(){
        new DeviceLocationPermissionsDialogFragment().show(getSupportFragmentManager(), "device_location_permissions");
    }

    @Override
    public void permissionNotAllowed() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.edit()
                .putBoolean("need_permissions_check", false)
                .apply();
    }

    @Override
    public void callPermissionsRequest() {
        checkPermissions();
    }
}
