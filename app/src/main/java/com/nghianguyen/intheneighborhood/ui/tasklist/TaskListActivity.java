package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.nghianguyen.intheneighborhood.alert.ProximityCheckReceiver;
import com.nghianguyen.intheneighborhood.alert.ProximityAlertManager;
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

public class TaskListActivity extends GoogleApiConnectActivity implements TaskListContract.View{
    public static final int REQUEST_CODE_TASK_DELETED = 100;

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
            presenter.updateProximityAlerts(ProximityAlertManager.get(this));

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean prefGps = sharedPrefs.getBoolean("pref_gps", false);

            Intent intent = new Intent(this, ProximityCheckReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1000, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if(prefGps){
                alarmManager.cancel(pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000 * 1, pendingIntent);
            }else{
                alarmManager.cancel(pendingIntent);
            }

        }else if(requestCode == REQUEST_CODE_TASK_DELETED){

            if(resultCode == RESULT_OK) {
                presenter.refreshTasks();
            }
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
        TaskListItemPresenter taskListItemPresenter = new TaskListItemPresenter(tasks, taskList);
        mAdapter = new TaskAdapter(this, taskListItemPresenter){
            @Override
            void startActivityForResult(Intent intent) {
                TaskListActivity.this.startActivityForResult(intent, REQUEST_CODE_TASK_DELETED);
            }
        };

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

    @Override
    public void displayMessage(String message) {
        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
