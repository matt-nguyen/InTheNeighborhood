package com.unlimitedrice.intheneighborhood;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/***************************************************
 * Singleton that holds and manages all the Tasks
 *
 ***************************************************/

public class TaskManager {

    private static TaskManager sTaskManager;
    private Context mContext;
    private ArrayList<Task> mTasks;
    private TaskJSONSerializer mTaskJsonSerializer;

    private TaskManager(Context c){
        mContext = c;

        mTaskJsonSerializer = new TaskJSONSerializer(c, "TASKS.json");
//        mTasks = new ArrayList<>();

        mTasks = mTaskJsonSerializer.loadTasks();
    }

    public static TaskManager get(Context c){
        if(sTaskManager == null){
            sTaskManager = new TaskManager(c.getApplicationContext());
        }
        return sTaskManager;
    }

    public ArrayList<Task> getTasks(){
        return mTasks;
    }

    public Task getTask(UUID id){
        if(id != null){
            for(Task t: mTasks){
                if(t.getId().equals(id))
                    return t;
            }
        }
        return null;
    }

    public void addTask(Task t){
        if(mTasks == null)
            mTasks = new ArrayList<>();

        mTasks.add(t);
    }

    public void deleteTask(Task t){
        if(mTasks != null) mTasks.remove(t);
    }

    public void clearTasks(){
        if(mTasks != null) {
            mTasks.clear();
        }
    }

    public void saveTasks(){
        mTaskJsonSerializer.saveTasks(mTasks);
    }


}
