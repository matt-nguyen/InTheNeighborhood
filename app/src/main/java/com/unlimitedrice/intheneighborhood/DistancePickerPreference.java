package com.unlimitedrice.intheneighborhood;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by unlim on 10/12/2017.
 */

public class DistancePickerPreference extends DialogPreference {
    private NumberPicker numberPicker;
    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 10;
    private final boolean WRAP_SELECTOR_WHEEL = true;

    private int value;

    public DistancePickerPreference(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.distancepicker_dialog, null, false);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.distance);
        numberPicker.setMinValue(MIN_VALUE);
        numberPicker.setMaxValue(MAX_VALUE);
        numberPicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        numberPicker.setValue(value);
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        value = (restorePersistedValue) ? getPersistedInt(MIN_VALUE) : (int) defaultValue;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
            numberPicker.clearFocus();
            int newValue = numberPicker.getValue();
            if(callChangeListener(newValue)) {
                persistInt(newValue);
            }
        }
    }
}
