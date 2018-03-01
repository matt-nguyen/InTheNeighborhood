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

import com.nghianguyen.intheneighborhood.R;

public class TaskView implements TaskContact.View{

    public EditText descriptionEditText;
    public Button selectPlaceButton;
    public CheckBox isDoneCheckbox;

    private TaskContact.Presenter presenter;

    public TaskView(Activity activity){
        descriptionEditText = activity.findViewById(R.id.descriptionEditText);
        selectPlaceButton = activity.findViewById(R.id.selectPlaceButton);
        isDoneCheckbox = activity.findViewById(R.id.isDoneCheckBox);

    }

    @Override
    public void setPresenter(TaskContact.Presenter presenter) {
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
            selectPlaceButton.setText(locName);
        }
    }

    private void setup(){
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                presenter.setDescription(editable.toString());
            }
        });

        isDoneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                presenter.setDone(b);
            }
        });

        selectPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.pickPlace();
            }
        });
    }
}