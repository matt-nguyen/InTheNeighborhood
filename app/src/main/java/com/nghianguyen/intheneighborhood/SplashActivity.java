package com.nghianguyen.intheneighborhood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nghianguyen.intheneighborhood.ui.tasklist.TaskListActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(
                new Intent(this, TaskListActivity.class)
        );
        finish();
    }
}
