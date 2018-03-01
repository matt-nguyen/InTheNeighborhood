package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.nghianguyen.intheneighborhood.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListView extends RelativeLayout{
    @BindView(R.id.task_recycler_view) public ContextMenuRecyclerView taskList;
    @BindView(R.id.fab) public FloatingActionButton fab;

    public TaskListView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
