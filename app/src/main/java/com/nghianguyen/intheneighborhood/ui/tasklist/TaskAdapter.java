package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.data.Task;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{

    private List<Task> mTasks;
    private Context mContext;
    private ContextMenuRecyclerView recyclerView;

    public TaskAdapter(Context c, List<Task> tasks, ContextMenuRecyclerView recyclerView){
        mContext = c;
        mTasks = tasks;
        this.recyclerView = recyclerView;
        Log.d("TaskAdapter", "Task size - " + mTasks.size());
    }

    public void refresh(List<Task> updatedTasks){
        mTasks = updatedTasks;
        notifyDataSetChanged();
    }

    /****************************************************************
     * Where we inflate layouts to create and return the ViewHolder
     * @param parent
     * @param viewType
     * @return
     ****************************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the inflater from the parent's context
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate layout of the list item
        View taskView = inflater.inflate(R.layout.item_task2, parent, false);
        Log.d("onCreateViewHolder", "layout inflated");
        return new ViewHolder(taskView);
    }

    /**************************************************************
     *  Where we populate data from the task onto the ViewHolder
     **************************************************************/
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Task task = mTasks.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recyclerView.showContextMenuForChild(view);
                return true;
            }
        });

        holder.itemView.setAlpha(
                (task.isDone()) ? 0.5f : 1.0f
        );

        holder.nearbyView.setVisibility(
                (!task.isDone() && task.isNearby()) ? View.VISIBLE : View.GONE
        );

        holder.descriptionTextView.setText(task.getDescription());
        holder.locNameTextView.setText(task.getLocName());
        holder.locAddrTextView.setText(task.getLocAddress());
        holder.locMapImageView.setImageBitmap(task.getLocMapImage());

        // Clicking on the task viewholder will open the TaskActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TaskActivity.class);
                intent.putExtra(TaskActivity.EXTRA_TASK_ID,
                        mTasks.get(holder.getAdapterPosition()).getDb_id());

                ((Activity)mContext).startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public Task getTask(int position){
        return mTasks.get(position);
    }

    public void updateNearbyTasks(Location location){
        if(location == null) return;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int proximityDistance = sharedPrefs.getInt("pref_distance", 1);

        List<Task> nearbyTasks = new ArrayList<>();

        LatLng locLatLng;
        Location taskLocation;
        for (Task task : mTasks) {
            locLatLng = task.getLocLatLng();
            if(locLatLng != null){
                taskLocation = new Location("");
                taskLocation.setLatitude(locLatLng.latitude);
                taskLocation.setLongitude(locLatLng.longitude);

                if(!task.isDone() && proximityDistance >= (location.distanceTo(taskLocation) / 1609)){
                    task.setNearby(true);
                    nearbyTasks.add(task);
                }else{
                    task.setNearby(false);
                }
            }
        }

        // Shift nearby tasks to the top
        mTasks.removeAll(nearbyTasks);
        mTasks.addAll(0, nearbyTasks);


        notifyDataSetChanged();
    }


    // Provides direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder{

        // Holder should have member variables for any view that will be set
        // in each row
        public TextView descriptionTextView;
        public TextView locNameTextView;
        public TextView locAddrTextView;
        public ImageView locMapImageView;
        public View nearbyView;
        public View taskItemView;

        // Parameter is the entire item row view
        public ViewHolder(View v){
            super(v);
            taskItemView = v;
            descriptionTextView = (TextView)v.findViewById(R.id.descriptionTextView);
            locNameTextView = (TextView)v.findViewById(R.id.locNameTextView);
            locAddrTextView = (TextView)v.findViewById(R.id.locAddrTextView);
            locMapImageView = (ImageView)v.findViewById(R.id.locMapImageView);
            nearbyView = v.findViewById(R.id.nearby_indicator);

        }

    }

}