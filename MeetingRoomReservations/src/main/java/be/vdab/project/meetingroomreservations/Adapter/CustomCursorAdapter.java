package be.vdab.project.meetingroomreservations.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import be.vdab.project.meetingroomreservations.Helper.DateHelper;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;

public class CustomCursorAdapter extends CursorAdapter {

    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reservations_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView dateView = (TextView) view.findViewById(R.id.dateAndTime);
        long beginDateLong = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)));
        long endDateLong = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)));
        String beginAndEndDate = DateHelper.formatDayFromMilis(beginDateLong) + "\n" + DateHelper.formatHoursMinutesFromMilis(beginDateLong) + "  -  " + DateHelper.formatHoursMinutesFromMilis(endDateLong);
        dateView.setText(beginAndEndDate);

        TextView nameView = (TextView) view.findViewById(R.id.userName);
        nameView.setText(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)));
    }
}
