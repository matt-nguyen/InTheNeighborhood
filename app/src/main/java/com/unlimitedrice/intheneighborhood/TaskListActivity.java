package com.unlimitedrice.intheneighborhood;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

public class TaskListActivity extends GoogleApiConnectActivity {

    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TESTING", "received broadcast");
            mTasks = getTasks();
            mAdapter.refresh(mTasks);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Log.d("TESTING", "onCreate TaskListActivity");
        TaskListView view = findViewById(R.id.content);

        mTasks = getTasks();
        mAdapter = new TaskAdapter(this, mTasks);

        RecyclerView recyclerView = view.taskList;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
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
                // Add new blank Task to the singleton
                Log.d("TESTING", "new task clicked");
//                startActivityForResult(
//                        new Intent(this, TaskActivity.class),
//                        TaskActivity.REQUEST_CODE
//                );
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SettingsActivity.REQUEST_CODE){
            Log.d("TESTING", "Settings done");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Log.d("TESTING", "gps? - " + prefs.getBoolean("pref_gps", true));
            Log.d("TESTING", "distance? - " + prefs.getInt("pref_distance", 1));
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


    private ArrayList<Task> getTasks(){
        return TaskDbManager.get(this).getTasks();
    }
}
