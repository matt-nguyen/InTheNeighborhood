package com.unlimitedrice.intheneighborhood;

import android.*;
import android.Manifest;
import android.content.Intent;
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

public class MainActivity extends GoogleApiConnectActivity {

    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTasks = TaskManager.get(this).getTasks();

        mAdapter = new TaskAdapter(this, mTasks);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // TODO: Add location proximity alerts
        // TODO: Add mapfragment under recyclerview to show currrent location and all task locations
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
                TaskManager taskManager = TaskManager.get(this);
                Task task = new Task();
                taskManager.addTask(task);

                Intent intent = new Intent(this, TaskActivity.class);
//                intent.putExtra(TaskActivity.EXTRA_TASK_POS, taskManager.getTasks().size()-1);
                Log.d("MainActivityLog", "Starting new task with id = " + task.getId());
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
        Log.d("onActivityResult", "Here");
        mAdapter.notifyDataSetChanged();

        TaskManager.get(this).saveTasks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("onRequestPermissionsRe", "ACCESS_FINE_LOCATION access granted");
            }
        }
    }


}
