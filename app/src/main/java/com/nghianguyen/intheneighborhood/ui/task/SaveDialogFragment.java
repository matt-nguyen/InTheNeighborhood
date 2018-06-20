package com.nghianguyen.intheneighborhood.ui.task;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.nghianguyen.intheneighborhood.R;

public class SaveDialogFragment extends DialogFragment {
    private static final String EXTRA_DIALOG = "dialog";

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

    public static SaveDialogFragment newInstance(int stringId){
        Bundle args = new Bundle();
        args.putInt(EXTRA_DIALOG, stringId);

        SaveDialogFragment fragment = new SaveDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int titleStringId = getArguments().getInt(EXTRA_DIALOG, R.string.dialog_save_changes_title);
        return new AlertDialog.Builder(getActivity())
                .setTitle(titleStringId)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.saveChanges(false);
                        dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.saveChanges(true);
                        dismiss();
                    }
                }).create();
    }

    interface Listener{
        void saveChanges(boolean yes);
    }
}
