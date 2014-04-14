package be.vdab.project.meetingroomreservations.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import be.vdab.project.meetingroomreservations.Activity.AddReservationActivity;
import be.vdab.project.meetingroomreservations.Activity.LoadingActivity;
import be.vdab.project.meetingroomreservations.Adapter.CustomCursorReservationsForDayAdapter;
import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Dialogs.DeleteOrEditConfirmationDialogFragment;
import be.vdab.project.meetingroomreservations.Model.Reservation;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_RESERVATION;

public class ReservationsForDayFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, DeleteOrEditConfirmationDialogFragment.Callback {

	private static final int LOADER_RESERVATIONS = 1;
	
	private Callbacks listener = dummyListener;
	
	private CustomCursorReservationsForDayAdapter adapter;
	
	private View view;

    private String reservationIdString;
    private Reservation res;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_reservations, container, false);

        getLoaderManager().initLoader(LOADER_RESERVATIONS, null, this);

        //query arguments



      /*  Uri reservationURI = CONTENT_URI_RESERVATION;
        String[] projection = {  DB.RESERVATIONS.ID, DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.personName , DB.RESERVATIONS.description};
        String selection = null;
        String[] selectionArgs  = null;

        Cursor cursor =  getActivity().getContentResolver().query(reservationURI,projection, selection , selectionArgs, "reservations.beginDate ASC" );
        cursor.moveToFirst();*/
/*
        MatrixCursor matrixCursor = new MatrixCursor(new String[] {  DB.RESERVATIONS.personName, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.description, DB.RESERVATIONS.ID});
        Log.e("matrixCursor", matrixCursor.toString());


        long begin = 0;
        long end = getTodayZeroHours().toMillis(false);
        int id = 10000000;
        while (cursor.moveToNext()) {
            begin =Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)));
            if((begin-end) >= 15*60*1000){


                matrixCursor.addRow(new Object[] { " ",end, begin, "hier een reservatie aanmaken" , id++ });
                matrixCursor.addRow(new Object[] {cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)),cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.description)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.ID))});

            }
            end= Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)));

        }
        Time tomorrow = getTodayZeroHours();
        tomorrow.set(tomorrow.toMillis(false) +(24 * 60 * 60 * 1000)-1);
        matrixCursor.addRow(new Object[] { " ",end, tomorrow.toMillis(false), "hier een reservatie aanmaken" , id++ });*/


       adapter = new CustomCursorReservationsForDayAdapter(getActivity(), null, 0); //todo: use the correct rows put into a MatrixCursor and show this with the adapter



		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setAdapter(adapter);
		
		list.setEmptyView(view.findViewById(android.R.id.empty));
		
		return view;
	}

    private Time getTodayZeroHours() {
        Time today = new Time();
        today.setToNow();
        today.hour = 0;
        today.minute = 0;
        today.second = 0;
        return today;
    }

    @Override
    public void onDelete() {
        new DeleteTask().execute();

    }

    @Override
    public void onEdit(){
        Intent intent = new Intent(getActivity().getApplicationContext(), AddReservationActivity.class);

        String tempId = ""+getActivity().getIntent().getExtras().get(Constants.MEETINGROOM_ID);
        String tempName =""+ getActivity().getIntent().getExtras().get(Constants.MEETINGROOM_NAME);
        Log.e("MeetingRoomActivity in ReservationsActivity: ", "string so it's not null: " + tempId);
        intent.putExtra(Constants.MEETINGROOM_ID, tempId);
        intent.putExtra(Constants.MEETINGROOM_NAME, tempName);

        intent.putExtra("beginDate", res.getBeginDate());
        intent.putExtra("endDate", res.getEndDate());
        intent.putExtra("personName", res.getPersonName());
        intent.putExtra("description", res.getDescription());
        intent.putExtra("reservationId", res.getReservationId());
        startActivity(intent);
    }

    @Override
    public void onCancel() {
        //do nothing
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getListView().setLongClickable(true);
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

               // Toast.makeText(getActivity(), "postion: " + id, Toast.LENGTH_SHORT).show();

                Uri reservationURI = CONTENT_URI_RESERVATION;
                String[] projection = {DB.RESERVATIONS.ID, DB.RESERVATIONS.reservationId, DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.personName, DB.RESERVATIONS.description};
                String selection = DB.RESERVATIONS.ID + "  = ?";
                String[] selectionArgs  = { Long.toString(id)  };

                Cursor cursor =  getActivity().getContentResolver().query(reservationURI,projection, selection , selectionArgs, null );
                cursor.moveToFirst();


                int index = cursor.getColumnIndex(DB.RESERVATIONS.reservationId);
                reservationIdString = cursor.getString(index);
                delete(reservationIdString);
                res = new Reservation();
                res.setReservationId(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.reservationId)));
                res.setMeetingRoomId(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.meetingRoomId)));
                res.setBeginDate(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)));
                res.setEndDate(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)));
                res.setPersonName(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)));
                res.setDescription(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.description)));







                return true;
            }
        });
    }

    private void delete(String meetingRoomIdString) {
        DeleteOrEditConfirmationDialogFragment fragment = DeleteOrEditConfirmationDialogFragment.newInstance(this);
        fragment.show(getFragmentManager(), "deleteconfirmation");


    }


    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
        Log.e("test", "onlistclick");
		listener.onReservationSelected(id);

	}
	
	/*public void setActivateOnItemClick(boolean activateOnItemClick) {
		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}*/

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //FIXME: need all those fields?
        String[] projection = { DB.RESERVATIONS.ID, DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.personName, DB.RESERVATIONS.description};

		String selection = DB.RESERVATIONS.beginDate + "  > ? and " + DB.RESERVATIONS.beginDate + " < ?";
        Time now = getTodayZeroHours();
        Time tomorrow = getTodayZeroHours();
        tomorrow.set(tomorrow.toMillis(false)+(24 * 60 * 60 * 1000));

        Log.e("start of today in millis: ", ""+now.toMillis(false));
        Log.e("start of tomorrow in millis: ", "" + tomorrow.toMillis(false));
        String[] selectionArgs = {""+now.toMillis(false), ""+tomorrow.toMillis(false)} ;

		CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
				ReservationsContentProvider.CONTENT_URI_RESERVATION, projection, selection,
				selectionArgs, DB.RESERVATIONS.beginDate + " ASC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        MatrixCursor matrixCursor = new MatrixCursor(new String[] {  DB.RESERVATIONS.personName, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.description, DB.RESERVATIONS.ID});
        Log.e("matrixCursor", matrixCursor.toString());


        long begin = 0;
        long end = getTodayZeroHours().toMillis(false);
        int id = 10000000;
        while (cursor.moveToNext()) {
            begin =Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)));
            if((begin-end) >= 15*60*1000){


                matrixCursor.addRow(new Object[] { " ",end, begin, "hier een reservatie aanmaken" , id++ });

            }
            matrixCursor.addRow(new Object[] {cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)),cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.description)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.ID))});

            end= Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)));

        }
        Time tomorrow = getTodayZeroHours();
        tomorrow.set(tomorrow.toMillis(false) +(24 * 60 * 60 * 1000)-1);
        matrixCursor.addRow(new Object[] { " ",end, tomorrow.toMillis(false), "hier een reservatie aanmaken" , id++ });

        Log.e("fhdsuifhfgs", "dkjfhdjkshfdfhsu");

      //  adapter = new CustomCursorReservationsForDayAdapter(getActivity(), matrixCursor, 0); //todo: use the correct rows put into a MatrixCursor and show this with the adapter


        adapter.swapCursor(matrixCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter = null;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		listener = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = dummyListener;
	}
	
	public interface Callbacks {
		public void onReservationSelected(Long id);
	}

	private static Callbacks dummyListener = new Callbacks() {
		@Override
		public void onReservationSelected(Long id) {
		}
	};


    class DeleteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                restTemplate.put(Constants.DATA_BASEURL + "reservations/deleteReservation/" + reservationIdString, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            //todo: use datarefreshservice instead of loadingactivity
            Intent intent = new Intent(getActivity().getApplicationContext(), LoadingActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

}
