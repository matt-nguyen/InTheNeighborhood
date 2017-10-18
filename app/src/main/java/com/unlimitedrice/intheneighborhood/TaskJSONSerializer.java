package com.unlimitedrice.intheneighborhood;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

/***************************************************
 * Class saves and loads task data from a JSON file
 *
 ***************************************************/

public class TaskJSONSerializer {

    private Context mContext;
    private String mFileName;

    public TaskJSONSerializer(Context c, String fileName){
        mContext = c.getApplicationContext();
        mFileName = fileName;
    }

    public void saveTasks(ArrayList<Task> tasks){
        JSONArray jsonArray = new JSONArray();

        OutputStream out = null;
        try {
            for(Task task : tasks){
                jsonArray.put(task.toJson());
            }

            out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            out.write(jsonArray.toString().getBytes());

            Log.d("saveTasks", "Tasks have been saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Task> loadTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = mContext.openFileInput(mFileName);
            br = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null){
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for(int i = 0; i < array.length(); i++){
                tasks.add(new Task(array.getJSONObject(i)));
            }

            Log.d("loadTasks", "Tasks count loaded - " + tasks.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return tasks;
    }
}
