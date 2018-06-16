package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.data.model.Task;
import com.nghianguyen.intheneighborhood.ui.task.TaskActivity;
import com.nghianguyen.intheneighborhood.ui.tasklist.adapter.TaskListItemPresenter;
import com.nghianguyen.intheneighborhood.ui.tasklist.adapter.TaskListItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nghianguyen.intheneighborhood.ui.tasklist.TaskListActivity.REQUEST_CODE_TASK_DELETED;

public abstract class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{

    private Context context;

    private TaskListItemPresenter presenter;

    public TaskAdapter(Context c, TaskListItemPresenter presenter){
        context = c;
        this.presenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View taskView = inflater.inflate(R.layout.item_task2, parent, false);
        Log.d("onCreateViewHolder", "layout inflated");
        return new ViewHolder(taskView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        presenter.onBindViewAtPosition(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra(TaskActivity.EXTRA_TASK_ID,
                        presenter.getTask(holder.getAdapterPosition()).getDb_id());

                startActivityForResult(intent);
//                ((Activity) context).startActivityForResult(intent, REQUEST_CODE_TASK_DELETED);
            }
        });

    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    public void refresh(List<Task> updatedTasks){
        presenter.refresh(updatedTasks);
        notifyDataSetChanged();
    }

    public Task getTask(int position){
        return presenter.getTask(position);
    }

    public void updateNearbyTasks(Location location){
        if(location == null) return;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int proximityDistance = sharedPrefs.getInt("pref_distance", 1);

        presenter.updateNearbyTasks(location, proximityDistance);

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements TaskListItemView{

        @BindView(R.id.descriptionTextView) public TextView descriptionTextView;
        @BindView(R.id.locNameTextView) public TextView locNameTextView;
        @BindView(R.id.locAddrTextView) public TextView locAddrTextView;
        @BindView(R.id.locMapImageView) public ImageView locMapImageView;
        @BindView(R.id.nearby_indicator) public View nearbyView;

        private TaskListItemPresenter presenter;

        public ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
            descriptionTextView = v.findViewById(R.id.descriptionTextView);
            locNameTextView = v.findViewById(R.id.locNameTextView);
            locAddrTextView = v.findViewById(R.id.locAddrTextView);
            locMapImageView = v.findViewById(R.id.locMapImageView);
            nearbyView = v.findViewById(R.id.nearby_indicator);

            setClickEvents();
        }

        private void setClickEvents(){
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    presenter.showContextMenu(v);
                    return true;
                }
            });
        }

        @Override
        public void setPresenter(TaskListItemPresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void displayTask(Task task) {
            descriptionTextView.setText(task.getDescription());
            locNameTextView.setText(task.getLocName());
            locAddrTextView.setText(task.getLocAddress());
            locMapImageView.setImageBitmap(task.getLocMapImage());
        }

        @Override
        public void setDone(boolean isDone) {
            itemView.setAlpha(
                    (isDone) ? 0.5f : 1.0f
            );
        }

        @Override
        public void showIsNearby(boolean isNearby) {
            nearbyView.setVisibility(
                    (isNearby) ? View.VISIBLE : View.GONE
            );
        }
    }

    abstract void startActivityForResult(Intent intent);
}