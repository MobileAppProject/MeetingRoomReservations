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

public class CustomCursorReservationsForDayAdapter extends CursorAdapter {

    public CustomCursorReservationsForDayAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reservations_for_day_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView dateBeginView = (TextView) view.findViewById(R.id.reservationsForDayReservationBeginTime);
        TextView dateEndView = (TextView) view.findViewById(R.id.reservationsForDayReservationEndTime);
        long beginDateLong = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)));
        long endDateLong = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)));
        dateBeginView.setText(DateHelper.formatHoursMinutesFromMilis(beginDateLong));
        dateEndView.setText(DateHelper.formatHoursMinutesFromMilis(endDateLong));

        TextView nameView = (TextView) view.findViewById(R.id.reservationsForDayPersonName);
        nameView.setText(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)));

        TextView descriptionView = (TextView) view.findViewById(R.id.reservationsForDayReservationDescription);
        descriptionView.setText(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.description)));
    }
}
