package com.unlimitedrice.intheneighborhood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by unlim on 9/21/2017.
 */

public class TaskDbManager {
    private static TaskDbManager taskDbManager;
    private TaskOpenHelper dbHelper;

    private TaskDbManager(Context context){
        dbHelper = new TaskOpenHelper(context);

    }

    public static TaskDbManager get(Context context){
        if(taskDbManager == null){
            taskDbManager = new TaskDbManager(context);
        }

        return taskDbManager;
    }

    public ArrayList<Task> getTasks(){
        return dbHelper.getTasks();
    }

    public Task getTask(int id){
        return dbHelper.getTask(id);
    }

    public void addTask(Task t){
        dbHelper.addTask(t);
    }

    public void clearTasks(){
        dbHelper.clearTasks();
    }
}
