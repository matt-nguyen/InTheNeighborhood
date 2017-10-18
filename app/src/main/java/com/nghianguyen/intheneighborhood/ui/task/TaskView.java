package com.nghianguyen.intheneighborhood.ui.task;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;

import com.nghianguyen.intheneighborhood.R;

public class TaskView {

    public EditText descriptionEditText;
    public Button selectPlaceButton;

    public TaskView(Activity activity){
        descriptionEditText = activity.findViewById(R.id.descriptionEditText);
        selectPlaceButton = activity.findViewById(R.id.selectPlaceButton);
    }
}
