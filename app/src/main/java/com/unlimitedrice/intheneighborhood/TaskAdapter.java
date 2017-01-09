package com.unlimitedrice.intheneighborhood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by unlim on 12/8/2016.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements
        View.OnCreateContextMenuListener{

    private List<Task> mTasks;
    private Context mContext;
    private static int mPositionPressed;

    // Provides direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder{

        // Holder should have member variables for any view that will be set
        // in each row
        public TextView descriptionTextView;
        public TextView locNameTextView;

        // Parameter is the entire item row view
        public ViewHolder(View v){
            super(v);
            descriptionTextView = (TextView)v.findViewById(R.id.descriptionTextView);
            locNameTextView = (TextView)v.findViewById(R.id.locNameTextView);
        }
    }


    public TaskAdapter(Context c, List<Task> tasks){
        mContext = c;
        mTasks = tasks;
        Log.d("TaskAdapter", "Task size - " + mTasks.size());
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
        View taskView = inflater.inflate(R.layout.item_task, parent, false);
        Log.d("onCreateViewHolder", "layout inflated");
        return new ViewHolder(taskView);
    }

    /**************************************************************
     *  Where we populate data from the task onto the ViewHolder
     **************************************************************/
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Task task = mTasks.get(position);
        Log.d("onBindViewHolder", "Position - " + position + " : " + task.getDescription()
                + " - " + task.getLocName());

        holder.descriptionTextView.setText(task.getDescription());
        holder.locNameTextView.setText(task.getLocName());

        // TODO: [DESIGN] Distinguish when a task is done

        // Clicking on the task viewholder will open the TaskActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TaskActivity.class);
//                intent.putExtra(TaskActivity.EXTRA_TASK_POS, position);
                intent.putExtra(TaskActivity.EXTRA_TASK_ID, mTasks.get(position).getId());

                ((Activity)mContext).startActivityForResult(intent, 0);
            }
        });

        // Update the position of the long clicked task so the context menu
        // would perform on the correct task
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mPositionPressed = holder.getAdapterPosition();
                Log.d("onLongClick", ""+mPositionPressed);
                return true;
            }
        });

        holder.itemView.setOnCreateContextMenuListener(this);
//        ((Activity)mContext).registerForContextMenu(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo) {
        Log.d("onCreateContextMenu", "in here");
//        ((Activity)mContext).getMenuInflater().inflate(R.menu.menu_context_task, contextMenu);
        contextMenu.setHeaderTitle("Testing");
        contextMenu.add(0, R.id.action_delete_task, 0, "Delete Task");
    }

    public static int getPositionPressed(){
        return mPositionPressed;
    }
}
