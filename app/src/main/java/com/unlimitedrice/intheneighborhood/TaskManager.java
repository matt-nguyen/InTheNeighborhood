package com.unlimitedrice.intheneighborhood;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by unlim on 12/20/2016.
 */

public class TaskManager {

    private static TaskManager sTaskManager;
    private Context mContext;
    private ArrayList<Task> mTasks;
    private TaskJSONSerializer mTaskJsonSerializer;

    private TaskManager(Context c){
        mContext = c;

        mTaskJsonSerializer = new TaskJSONSerializer(c, "stuff.json");
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

    public Task getTask(int pos){
        if(pos > -1 && pos < mTasks.size()){
            return mTasks.get(pos);
        }
        return null;
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
        if(mTasks != null) mTasks.clear();
    }

    public void saveTasks(){
        mTaskJsonSerializer.saveTasks(mTasks);
    }

    /*************************************************************
     * Returns true if the alertId is not currently set to a task
     *************************************************************/
    public boolean isAlertIdAvailable(int alertId){
        for(Task task: mTasks){
            if(task.getAlertId() == alertId) return false;
        }
        return true;
    }

    /******************************************
     * Randomly generates an available int id
     ******************************************/
    public int generateAlertId(){
        int MAX_VALUE = Integer.MAX_VALUE;

        int alertId;

        do{
            alertId = (int)(Math.random() * MAX_VALUE);
        }while(!isAlertIdAvailable(alertId));

        return alertId;
    }
}
