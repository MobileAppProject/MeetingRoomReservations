package be.vdab.project.meetingroomreservations.Activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Event;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_RESERVATION;

public class ExtendedCalendarActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String[] projection = { DB.RESERVATIONS.personName, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.ID };
        String selection = null;
        String[] selectionArgs  = null;


        Uri reservationURI = CONTENT_URI_RESERVATION;
        Cursor cursor =  getContentResolver().query(reservationURI, projection, selection, selectionArgs, null);
        //cursor.moveToFirst();

        ContentValues values = new ContentValues();
        values.put(CalendarProvider.COLOR, Event.COLOR_RED);
        values.put(CalendarProvider.DESCRIPTION, "Some Description");
        values.put(CalendarProvider.LOCATION, "Some location");
        values.put(CalendarProvider.EVENT, "Event name");

        Calendar cal = Calendar.getInstance();
        Log.e("testing calendar oncreate", "");


        //todo: cursor seems to be empty, calendar does not get filled
        while (cursor.moveToNext()) {
            //  cal.set(2014, 3, 16, 10, 30);
            cal.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate))));
            values.put(CalendarProvider.START, cal.getTimeInMillis());
            TimeZone tz = TimeZone.getDefault();

            int beginDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

            values.put(CalendarProvider.START_DAY, beginDayJulian);


            // cal.set(2014, 3, 16, 12, 30);
            int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

            values.put(CalendarProvider.END, cal.getTimeInMillis());
            values.put(CalendarProvider.END_DAY, endDayJulian);

            Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
            Log.e("reservation should have been added to calendar/month view", "");
        }




        setContentView(R.layout.activity_extended_calendar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.extended_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = { DB.RESERVATIONS.ID, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.personName, DB.RESERVATIONS.reservationId};

        String selection = null;
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
                ReservationsContentProvider.CONTENT_URI_RESERVATION, projection, selection,
                null, DB.RESERVATIONS.beginDate + " ASC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
