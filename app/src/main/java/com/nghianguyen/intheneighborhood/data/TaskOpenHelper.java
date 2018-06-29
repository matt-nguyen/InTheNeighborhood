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
import com.nghianguyen.intheneighborhood.data.model.Task;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TaskOpenHelper extends SQLiteOpenHelper {
    public static final String DB_UPDATED = "com.nghianguyen.intheneighborhood.TaskOpenHelper.DB_UPDATED";

    private static final String DATABASE_NAME = "tasks";
    private static final int DATABASE_VERSION = 3;

    public static final String TASK_TABLE_NAME = "tasks";
    public static final String FIELD_TASK_ID = "task_id";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LOC_NAME = "loc_name";
    public static final String FIELD_LOC_ADDR = "loc_address";
    public static final String FIELD_LOC_LAT = "loc_lat";
    public static final String FIELD_LOC_LNG = "loc_lng";
    public static final String FIELD_LOC_MAP_IMAGE = "loc_map_image";
    public static final String FIELD_IS_DONE = "is_done";

    private static final String TASK_TABLE_CREATE =
            "CREATE TABLE " + TASK_TABLE_NAME + " (" +
                    FIELD_TASK_ID + " INTEGER PRIMARY KEY, " +
                    FIELD_DESCRIPTION + " TEXT NOT NULL, " +
                    FIELD_LOC_NAME + " TEXT, " +
                    FIELD_LOC_ADDR + " TEXT, " +
                    FIELD_LOC_LAT + " REAL, " +
                    FIELD_LOC_LNG + " REAL, " +
                    FIELD_LOC_MAP_IMAGE + " BLOB, " +
                    FIELD_IS_DONE + " INTEGER " +
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
        sqLiteDatabase.execSQL(TASK_TABLE_DELETE);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Task> getTasks(){
        return getTasks(FIELD_IS_DONE + " ASC");
    }

    public ArrayList<Task> getTasks(String orderBy){

        ArrayList<Task> tasks = new ArrayList<>();

        String[] columns = new String[]{
                FIELD_TASK_ID,
                FIELD_DESCRIPTION,
                FIELD_LOC_NAME,
                FIELD_LOC_ADDR,
                FIELD_LOC_LAT,
                FIELD_LOC_LNG,
                FIELD_LOC_MAP_IMAGE,
                FIELD_IS_DONE
        };

        Cursor cursor = getReadableDatabase().query(
                TaskOpenHelper.TASK_TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                orderBy
        );

        int rowId, isDoneInt;
        String description, locName, locAddr;
        double lat, lng;
        byte[] blob;
        Bitmap locMapImage;
        boolean isDone;
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

                isDoneInt = cursor.getInt(cursor.getColumnIndex(FIELD_IS_DONE));
                isDone = isDoneInt == 1;

                tasks.add(
                        new Task(rowId, description, locName, locAddr, lat, lng, locMapImage, isDone)
                );
            }catch (Exception ex){
                ex.printStackTrace();
            }
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
                FIELD_LOC_MAP_IMAGE,
                FIELD_IS_DONE
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

        int rowId, isDoneInt;
        String description, locName, locAddr;
        double lat, lng;
        byte[] blob;
        Bitmap locMapImage;
        boolean isDone;
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

                isDoneInt = cursor.getInt(cursor.getColumnIndex(FIELD_IS_DONE));
                isDone = isDoneInt == 1;

                task = new Task(rowId, description, locName, locAddr, lat, lng, locMapImage, isDone);
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                break;
            }
        }

        return task;
    }

    public long addTask(Task t){
        ContentValues cv = buildContentValues(t);

        long id = getWritableDatabase().insert(TASK_TABLE_NAME, null, cv);
        if(id != -1){
            notifyUpdate();
        }

        return id;
    }

    public long updateTask(Task t){
        ContentValues cv = buildContentValues(t);

        long id = getWritableDatabase().update(
                TASK_TABLE_NAME,
                cv,
                "rowid = ?",
                new String[]{String.valueOf(t.getDb_id())});
        if(id != -1){
            notifyUpdate();
        }

        return id;
    }

    public int deleteTask(Task t){
        String whereClause = "rowid = ?";
        String[] whereArgs = new String[]{String.valueOf(t.getDb_id())};

        return getWritableDatabase().delete(TASK_TABLE_NAME, whereClause, whereArgs);
    }

    public void clearTasks(){
        getWritableDatabase().execSQL("DELETE FROM " + TASK_TABLE_NAME);
        notifyUpdate();
    }

    public void notifyUpdate(){
        Intent intent = new Intent(DB_UPDATED);
        context.sendBroadcast(intent);
    }

    public ContentValues buildContentValues(Task t){
        ContentValues cv = new ContentValues();
        cv.put(FIELD_DESCRIPTION, t.getDescription());
        cv.put(FIELD_LOC_NAME, t.getLocName());
        cv.put(FIELD_LOC_ADDR, t.getLocAddress());
        cv.put(FIELD_IS_DONE, t.isDone());

        LatLng locLatLng = t.getLocLatLng();
        if(locLatLng != null) {
            cv.put(FIELD_LOC_LAT, locLatLng.latitude);
            cv.put(FIELD_LOC_LNG, locLatLng.longitude);
        }else{
            cv.putNull(FIELD_LOC_LAT);
            cv.putNull(FIELD_LOC_LNG);
        }

        Bitmap locMapImage = t.getLocMapImage();
        if(locMapImage != null){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            locMapImage.compress(Bitmap.CompressFormat.PNG, 0, os);
            cv.put(FIELD_LOC_MAP_IMAGE, os.toByteArray());
        }else{
            cv.putNull(FIELD_LOC_MAP_IMAGE);
        }

        return cv;
    }
}
