package com.nghianguyen.intheneighborhood.ui.task;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.nghianguyen.intheneighborhood.R;
import com.nghianguyen.intheneighborhood.core.SimpleTextWatcher;

public class TaskView implements TaskContract.View{

    public EditText descriptionEditText;
    public Button selectPlaceButton;
    public CheckBox isDoneCheckbox;
    public EditText locationName;
    public TextView addressText;
    public View deleteButton;

    private TaskContract.Presenter presenter;

    public TaskView(Activity activity){
        descriptionEditText = activity.findViewById(R.id.descriptionEditText);
        selectPlaceButton = activity.findViewById(R.id.selectPlaceButton);
        isDoneCheckbox = activity.findViewById(R.id.isDoneCheckBox);
        locationName = activity.findViewById(R.id.location_name);
        addressText = activity.findViewById(R.id.location_address);
        deleteButton = activity.findViewById(R.id.delete_button);
    }

    @Override
    public void setPresenter(TaskContract.Presenter presenter) {
        this.presenter = presenter;

        if(presenter != null){
            setup();
        }
    }

    @Override
    public void displayDescription(String description) {
        descriptionEditText.setText(description);
    }

    @Override
    public void showTaskDone(boolean isDone) {
        isDoneCheckbox.setChecked(isDone);
    }

    @Override
    public void showLocationName(String locName) {
        if(!TextUtils.isEmpty(locName)) {
            selectPlaceButton.setText(R.string.button_update_place);
            locationName.setText(locName);
        }
    }

    @Override
    public boolean isLocationNameEntered() {
        return !TextUtils.isEmpty(locationName.getText());
    }

    @Override
    public void showLocationAddress(String address) {
        if(!TextUtils.isEmpty(address)) {
            addressText.setText(address);
        }else{
            addressText.setText(R.string.task_location_address_label);
        }
    }

    private void setup(){
        descriptionEditText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                presenter.setDescription(s.toString());
            }
        });

        locationName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                presenter.setLocationName(s.toString());
            }
        });

        isDoneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                presenter.markDoneStatus(b);
            }
        });

        selectPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.pickPlace();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.deleteTask();
            }
        });
    }
}
