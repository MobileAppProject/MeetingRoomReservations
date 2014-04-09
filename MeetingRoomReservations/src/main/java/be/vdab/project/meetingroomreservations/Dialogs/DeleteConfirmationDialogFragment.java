package be.vdab.project.meetingroomreservations.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


import be.vdab.project.meetingroomreservations.R;

/**
 * Created by Yannick on 18/02/14.
 */
public class DeleteConfirmationDialogFragment extends DialogFragment {

    private Callback listener;

    public interface Callback {
        void onConfirm();
        void onCancel();
    }

    public static DeleteConfirmationDialogFragment newInstance(Callback listener) {
        DeleteConfirmationDialogFragment fragment = new DeleteConfirmationDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_confirmation_dialog_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onConfirm();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCancel();
                    }
                });
        return builder.create();
    }

}
