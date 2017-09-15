package com.unlimitedrice.intheneighborhood;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TaskView {

    public EditText descriptionEditText;
    public Button selectPlaceButton;

    public TaskView(Activity activity){
        descriptionEditText = activity.findViewById(R.id.descriptionEditText);
        selectPlaceButton = activity.findViewById(R.id.selectPlaceButton);
    }
}
