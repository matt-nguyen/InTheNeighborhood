package com.nghianguyen.intheneighborhood.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.nghianguyen.intheneighborhood.R;

public class ProximityPickerPrefences extends DialogPreference {
    private NumberPicker numberPicker;
    private NumberPicker decimalPicker;
    private final int MIN_VALUE = 0;
    private final int MAX_VALUE = 10;
    private final int DECIMAL_MIN_VALUE = 0;
    private final int DECIMAL_MAX_VALUE = 9;
    private final boolean WRAP_SELECTOR_WHEEL = true;

    private float distanceValue;

    public ProximityPickerPrefences(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.dialog_proximitypicker, null, false);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        int number = (int)Math.floor(distanceValue);

        numberPicker = (NumberPicker) view.findViewById(R.id.distance);
        numberPicker.setMinValue(MIN_VALUE);
        numberPicker.setMaxValue(MAX_VALUE);
        numberPicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        numberPicker.setValue(number);

        int decimal = (int)(10 * (distanceValue - (float)number));

        decimalPicker = (NumberPicker) view.findViewById(R.id.distance_decimal);
        decimalPicker.setMinValue(DECIMAL_MIN_VALUE);
        decimalPicker.setMaxValue(DECIMAL_MAX_VALUE);
        decimalPicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        decimalPicker.setValue(decimal);
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        distanceValue = (restorePersistedValue) ? getPersistedFloat(1.0f) : 1.0f;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
            numberPicker.clearFocus();
            decimalPicker.clearFocus();
            float newValueFloat = numberPicker.getValue() + ((float)decimalPicker.getValue() / 10);
            if(callChangeListener(newValueFloat)){
                persistFloat(newValueFloat);
            }
        }
    }
}
