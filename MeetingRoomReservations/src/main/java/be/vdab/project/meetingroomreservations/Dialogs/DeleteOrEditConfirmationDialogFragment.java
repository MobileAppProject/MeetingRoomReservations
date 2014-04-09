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
public class DeleteOrEditConfirmationDialogFragment extends DialogFragment {

    private Callback listener;

    public interface Callback {
        void onDelete();
        void onCancel();
        void onEdit();
    }

    public static DeleteOrEditConfirmationDialogFragment newInstance(Callback listener) {
        DeleteOrEditConfirmationDialogFragment fragment = new DeleteOrEditConfirmationDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_or_edit_confirmation_dialog_message)
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDelete();
                    }
                })
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onEdit();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCancel();
                    }
                })
                ;
        return builder.create();
    }

}
