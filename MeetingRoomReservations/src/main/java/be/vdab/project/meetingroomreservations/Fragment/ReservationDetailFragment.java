package be.vdab.project.meetingroomreservations.Fragment;


import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Helper.DateHelper;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

public class ReservationDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //todo: everything
    private static final int LOADER_APPOINTMENT = 2;

    private View view;

    SimpleCursorAdapter adapter;

    private Long reservationId;
    private String appointmentBackendId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Constants.RESERVATION_ID)) {
            reservationId = getArguments().getLong(Constants.RESERVATION_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservation_detail, container, false);
        getLoaderManager().initLoader(LOADER_APPOINTMENT, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DB.RESERVATIONS.ID, DB.RESERVATIONS.personName, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.description, DB.RESERVATIONS.active, DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.reservationId};

        String selection = null;
        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
                ContentUris.withAppendedId(ReservationsContentProvider.CONTENT_URI_RESERVATION, reservationId), projection, selection,
                null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            TextView dateView = (TextView) view.findViewById(R.id.reservation_detail_date);
            //SimpleDateFormat dfDay = new SimpleDateFormat("E dd/MM/yyyy");
            //Date date = new Date (Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate))));
            dateView.setText(DateHelper.formatDayFromMilis(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)))));


            TextView beginView = (TextView) view.findViewById(R.id.reservation_detail_begin_time);
            beginView.setText(DateHelper.formatHoursMinutesFromMilis(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)))));

            TextView endView = (TextView) view.findViewById(R.id.reservation_detail_end_time);
            endView.setText(DateHelper.formatHoursMinutesFromMilis(Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)))));

            TextView personNameView = (TextView) view.findViewById(R.id.reservation_detail_name);
            personNameView.setText(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)));

            TextView descriptionView = (TextView) view.findViewById(R.id.reservation_detail_description);
            descriptionView.setText(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.description)));

            appointmentBackendId = cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.reservationId));
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        adapter = null;
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.appointment_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                DeleteConfirmationDialogFragment fragment = DeleteConfirmationDialogFragment.newInstance(this);
                fragment.show(getFragmentManager(), "deleteconfirmation");
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
//
//    @Override
//    public void onConfirm() {
//        new DeleteTask().execute();
//    }
//
//    @Override
//    public void onCancel() {
//        //do nothing
//    }
//
//    class DeleteTask extends AsyncTask<String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                RestTemplate restTemplate = new RestTemplate();
//                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
//
//                restTemplate.delete("http://192.168.56.1:2403/afspraken/" + appointmentBackendId);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String value) {
//            Intent intent = new Intent(getActivity().getApplicationContext(), LoadingActivity.class);
//            startActivity(intent);
//            getActivity().finish();
//        }
//
//    }


}
