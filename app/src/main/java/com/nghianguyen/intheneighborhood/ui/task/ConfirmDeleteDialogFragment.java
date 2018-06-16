package com.nghianguyen.intheneighborhood.ui.task;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.nghianguyen.intheneighborhood.R;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    TaskContract.View taskView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        taskView = (TaskContract.View) context;
    }

    @Override
    public void onDetach() {
        taskView = null;
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_confirm_delete_title)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskView.deleteTaskConfirmed();
                        dismiss();
                    }
                }).create();
    }
}
