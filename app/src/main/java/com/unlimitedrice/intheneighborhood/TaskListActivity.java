package com.unlimitedrice.intheneighborhood;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/************************************************************************
 * Front activity of app that displays list of all the user's tasks
 * From here, the user can:
 * - Initiate a new task
 * - Select an existing task for editing
 * - Clearing all tasks
 *
 ************************************************************************/

public class TaskListActivity extends GoogleApiConnectActivity {

    private static final String TAG = TaskListActivity.class.getName();

    public static final String INTENT_FILTER_NOTIFY =
            "com.unlimitedrice.intheneighborhood.notify_adapter";

    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mAdapter != null) {

                // As user leaves and enters task locations, "refresh" the task list to display
                // task proximity by color
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTasks = TaskManager.get(this).getTasks();

        mAdapter = new TaskAdapter(this, mTasks);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Add check for google play services

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // TODO:Figure out font to use for recyclerview items
        // TODO:Add mapfragment over recyclerview to show current location and all task locations
    }

    @Override
    public void onResume(){
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(INTENT_FILTER_NOTIFY);

        Log.d(TAG, "Registering receiver");
        registerReceiver(mReceiver, intentFilter);
    }


    @Override
    public void onPause(){
        super.onPause();
        TaskManager.get(this).saveTasks();

        if(mReceiver != null) {
            Log.d(TAG, "Unregistering receiver");
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
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
                Task task = new Task();
                mTasks.add(task);

                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());

                startActivityForResult(intent, 0);
                return true;
            case R.id.action_clear_all_tasks:
                // Delete all tasks
                TaskManager.get(this).clearTasks();
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.notifyDataSetChanged();
        TaskManager.get(this).saveTasks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "ACCESS_FINE_LOCATION access granted");
            }else{
                Log.d(TAG, "ACCESS_FINE_LOCATION not granted");
            }
        }
    }


}
