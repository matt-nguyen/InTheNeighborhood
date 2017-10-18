package com.nghianguyen.intheneighborhood.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TaskOpenHelper extends SQLiteOpenHelper {
    public static final String DB_UPDATED = "com.nghianguyen.intheneighborhood.TaskOpenHelper.DB_UPDATED";

    private static final String DATABASE_NAME = "tasks";
    private static final int DATABASE_VERSION = 2;

    public static final String TASK_TABLE_NAME = "tasks";
    public static final String FIELD_TASK_ID = "task_id";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LOC_NAME = "loc_name";
    public static final String FIELD_LOC_ADDR = "loc_address";
    public static final String FIELD_LOC_LAT = "loc_lat";
    public static final String FIELD_LOC_LNG = "loc_lng";
    public static final String FIELD_LOC_MAP_IMAGE = "loc_map_image";

    private static final String TASK_TABLE_CREATE =
            "CREATE TABLE " + TASK_TABLE_NAME + " (" +
                    FIELD_TASK_ID + " INTEGER PRIMARY KEY, " +
                    FIELD_DESCRIPTION + " TEXT NOT NULL, " +
                    FIELD_LOC_NAME + " TEXT, " +
                    FIELD_LOC_ADDR + " TEXT, " +
                    FIELD_LOC_LAT + " REAL, " +
                    FIELD_LOC_LNG + " REAL, " +
                    FIELD_LOC_MAP_IMAGE + " BLOB " +
                    ");";

    private static final String TASK_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + TASK_TABLE_NAME;

    private Context context;

    TaskOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TASK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("TESTING", "Upgrading from  " + i + ", to " + i1);
        sqLiteDatabase.execSQL(TASK_TABLE_DELETE);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Task> getTasks(){

        ArrayList<Task> tasks = new ArrayList<>();

        String[] columns = new String[]{
                FIELD_TASK_ID,
                FIELD_DESCRIPTION,
                FIELD_LOC_NAME,
                FIELD_LOC_ADDR,
                FIELD_LOC_LAT,
                FIELD_LOC_LNG,
                FIELD_LOC_MAP_IMAGE
        };

        Cursor cursor = getReadableDatabase().query(
                TaskOpenHelper.TASK_TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        int rowId;
        String description, locName, locAddr;
        double lat, lng;
        byte[] blob;
        Bitmap locMapImage;
        while(cursor.moveToNext()){
            try {
                rowId = cursor.getInt(cursor.getColumnIndex(FIELD_TASK_ID));
                description = cursor.getString(cursor.getColumnIndex(TaskOpenHelper.FIELD_DESCRIPTION));
                locName = cursor.getString(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_NAME));
                locAddr = cursor.getString(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_ADDR));
                lat = cursor.getDouble(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_LAT));
                lng = cursor.getDouble(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_LNG));
                blob = cursor.getBlob(cursor.getColumnIndex(FIELD_LOC_MAP_IMAGE));

                if(blob != null){
                    locMapImage = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                }else{
                    locMapImage = null;
                }
                tasks.add(
                        new Task(rowId, description, locName, locAddr, lat, lng, locMapImage)
                );
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        Log.d("TESTING", "got tasks from db");
        for (Task task : tasks) {
            Log.d("TESTING", task.toString());
        }

        return tasks;
    }

    public Task getTask(int id){
        Task task = null;
        String[] columns = new String[]{
                FIELD_TASK_ID,
                FIELD_DESCRIPTION,
                FIELD_LOC_NAME,
                FIELD_LOC_ADDR,
                FIELD_LOC_LAT,
                FIELD_LOC_LNG,
                FIELD_LOC_MAP_IMAGE
        };

        Cursor cursor = getReadableDatabase().query(
                TASK_TABLE_NAME,
                columns,
                FIELD_TASK_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        int rowId;
        String description, locName, locAddr;
        double lat, lng;
        byte[] blob;
        Bitmap locMapImage;
        while(cursor.moveToNext()){
            try {
                rowId = cursor.getInt(cursor.getColumnIndex(FIELD_TASK_ID));
                description = cursor.getString(cursor.getColumnIndex(TaskOpenHelper.FIELD_DESCRIPTION));
                locName = cursor.getString(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_NAME));
                locAddr = cursor.getString(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_ADDR));
                lat = cursor.getDouble(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_LAT));
                lng = cursor.getDouble(cursor.getColumnIndex(TaskOpenHelper.FIELD_LOC_LNG));
                blob = cursor.getBlob(cursor.getColumnIndex(FIELD_LOC_MAP_IMAGE));

                if(blob != null){
                    locMapImage = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                }else{
                    locMapImage = null;
                }

                task = new Task(rowId, description, locName, locAddr, lat, lng, locMapImage);
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                break;
            }
        }

        if(task != null){
            Log.d("TESTING", "found task - " + task.toString());
        }else{
            Log.d("TESTING", "didn't find task");
        }

        return task;
    }

    public long addTask(Task t){
        ContentValues cv = new ContentValues();
        cv.put(FIELD_DESCRIPTION, t.getDescription());
        cv.put(FIELD_LOC_NAME, t.getLocName());
        cv.put(FIELD_LOC_ADDR, t.getLocAddress());

        LatLng locLatLng = t.getLocLatLng();
        if(locLatLng != null) {
            cv.put(FIELD_LOC_LAT, locLatLng.latitude);
            cv.put(FIELD_LOC_LNG, locLatLng.longitude);
        }

        Bitmap locMapImage = t.getLocMapImage();
        if(locMapImage != null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            locMapImage.compress(Bitmap.CompressFormat.PNG, 0, os);
            cv.put(FIELD_LOC_MAP_IMAGE, os.toByteArray());
        }

        Log.d("TESTING", "inserting - " + cv.toString());
        long id = getWritableDatabase().insert(TASK_TABLE_NAME, null, cv);
        if(id == -1){
            Log.d("TESTING", "Error adding task to db");
        }else{
            notifyUpdate();
        }

        return id;
    }

    public long updateTask(Task t){
        ContentValues cv = new ContentValues();
        cv.put(FIELD_DESCRIPTION, t.getDescription());
        cv.put(FIELD_LOC_NAME, t.getLocName());
        cv.put(FIELD_LOC_ADDR, t.getLocAddress());

        LatLng locLatLng = t.getLocLatLng();
        if(locLatLng != null) {
            cv.put(FIELD_LOC_LAT, locLatLng.latitude);
            cv.put(FIELD_LOC_LNG, locLatLng.longitude);
        }

        Bitmap locMapImage = t.getLocMapImage();
        if(locMapImage != null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            locMapImage.compress(Bitmap.CompressFormat.PNG, 0, os);
            cv.put(FIELD_LOC_MAP_IMAGE, os.toByteArray());
        }

        Log.d("TESTING", "updating - " + cv.toString());
        long id = getWritableDatabase().update(
                TASK_TABLE_NAME,
                cv,
                "rowid = ?",
                new String[]{String.valueOf(t.getDb_id())});
        if(id == -1){
            Log.d("TESTING", "Error updating task to db");
        }else{
            notifyUpdate();
        }

        return id;
    }

    public void clearTasks(){
        getWritableDatabase().execSQL("DELETE FROM " + TASK_TABLE_NAME);
        notifyUpdate();
    }

    public void notifyUpdate(){
        Intent intent = new Intent(DB_UPDATED);
        context.sendBroadcast(intent);
    }
}
