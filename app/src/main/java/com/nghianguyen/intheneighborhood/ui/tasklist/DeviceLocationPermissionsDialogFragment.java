package com.nghianguyen.intheneighborhood.ui.tasklist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.nghianguyen.intheneighborhood.R;

public class DeviceLocationPermissionsDialogFragment extends DialogFragment{

    private Listener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Listener) context;
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_location_permission_title)
                .setMessage(R.string.dialog_location_permission_message)
                .setIcon(R.drawable.warning_icon)
                .setNegativeButton(R.string.dialog_location_permission_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.permissionNotAllowed();
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_location_permission_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.callPermissionsRequest();
                        dismiss();
                    }
                }).create();
    }

    interface Listener{
        void permissionNotAllowed();
        void callPermissionsRequest();
    }
}
