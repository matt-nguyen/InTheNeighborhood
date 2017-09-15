package com.unlimitedrice.intheneighborhood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListView extends RelativeLayout{
    @BindView(R.id.task_recycler_view) RecyclerView taskList;

    public TaskListView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
