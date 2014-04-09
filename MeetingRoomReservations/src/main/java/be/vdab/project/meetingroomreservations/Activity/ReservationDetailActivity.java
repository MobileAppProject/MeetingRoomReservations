package be.vdab.project.meetingroomreservations.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Fragment.ReservationDetailFragment;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

/**
 * Created by jeansmits on 7/04/14.
 */
public class ReservationDetailActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_RESERVATION_ID = 3;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        getSupportLoaderManager().initLoader(LOADER_RESERVATION_ID, null, this);

        viewPager = (ViewPager) findViewById(R.id.reservation_detail_viewpager);
        Log.e("", "oncreate - viewpager: " + viewPager);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = { DB.RESERVATIONS.ID}; // only ID?

        String selection = null;
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
                ReservationsContentProvider.CONTENT_URI_RESERVATION, projection, selection,
                null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e("", "onloadfinished");
        List<Long> reservationIds = new ArrayList<Long>();
        cursor.moveToFirst();
        do {
            reservationIds.add(cursor.getLong(cursor.getColumnIndex(DB.RESERVATIONS.ID)));
        } while(cursor.moveToNext());
        viewPager.setAdapter(new ReservationDetailPager(getSupportFragmentManager(), reservationIds));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private class ReservationDetailPager extends FragmentStatePagerAdapter {

        List<Long> reservationIds;

        public ReservationDetailPager(FragmentManager fm, List<Long> reservationIds) {
            super(fm);
            this.reservationIds = reservationIds;
        }


        @Override
        public Fragment getItem(int i) {
            Bundle arguments = new Bundle();
            arguments.putLong(Constants.RESERVATION_ID, reservationIds.get(i));
            ReservationDetailFragment fragment = new ReservationDetailFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public int getCount() {
            return reservationIds.size();
        }
    }
}
