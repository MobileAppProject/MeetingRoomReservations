package be.vdab.project.meetingroomreservations.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Yannick on 17/02/14.
 */
public class TimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Callback listener;

    public static TimePickerDialogFragment newInstance(Callback listener) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.listener = listener;
        return fragment;
    }


    public interface Callback {
        void onTimeSelected(String time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String s = "";
        if(hourOfDay < 10){
            s+="0";
        }
        s+=hourOfDay + ":";
        if(minute < 10){
            s+="0";
        }
        s+=minute;
        listener.onTimeSelected(s);
    }

}
