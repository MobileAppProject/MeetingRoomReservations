package be.vdab.project.meetingroomreservations.Dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Yannick on 17/02/14.
 */
public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Callback listener;
    private int year;
    private int month;
    private int day;


    public DatePickerDialogFragment(){
        //empty constructor
    }
    public DatePickerDialogFragment(int year, int month, int day){
        Log.e("year in DatePickerDialogFragment", "" + year);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public interface Callback {
        void onDateSelected(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(this.year == 0) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }


        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String s = ""+year + "-";
        if(month < 9){
            s+="0";
        }
        s+=month+1 + "-";
        if(day < 10){
            s+="0";
        }

        s+=day;


        listener.onDateSelected(s);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(!(activity instanceof Callback)) {
            throw new IllegalArgumentException("activity should implement DatePickerDialogFragment Callback");
        }
        listener = (Callback) activity;
    }
}
