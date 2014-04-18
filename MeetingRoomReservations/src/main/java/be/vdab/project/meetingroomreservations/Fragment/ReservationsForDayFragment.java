package be.vdab.project.meetingroomreservations.Fragment;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import be.vdab.project.meetingroomreservations.Activity.AddReservationActivity;
import be.vdab.project.meetingroomreservations.Activity.LoadingActivity;
import be.vdab.project.meetingroomreservations.Activity.ReservationsForDayActivity;
import be.vdab.project.meetingroomreservations.Adapter.CustomCursorReservationsForDayAdapter;
import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Dialogs.DeleteOrEditConfirmationDialogFragment;
import be.vdab.project.meetingroomreservations.Helper.DateHelper;
import be.vdab.project.meetingroomreservations.Model.Reservation;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_RESERVATION;

public class ReservationsForDayFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, DeleteOrEditConfirmationDialogFragment.Callback {

    private static final String TAG = "ReservationsForDayFragment";
	private static final int LOADER_RESERVATIONSFORDAY = 6871897;
    private static final long _24_HOURS_IN_MILIS = 24 * 60 * 60 * 1000;
	
	private Callbacks listener = dummyListener;
	
	private CustomCursorReservationsForDayAdapter adapter;
	
	private View view;

    TextView tvDay;
    Button btnPrevious, btnNext;

    private String reservationIdString;
    private Reservation res;

    private Boolean isInWeekView;
    private long givenDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInWeekView = getArguments().getBoolean(Constants.WEEKVIEW);
        givenDate = getArguments().getLong(Constants.DATE);

        Log.e(TAG, "onCreate: arguments: isInWeekView:" + isInWeekView + ", date: " + new Date(givenDate));
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_reservations_for_day, container, false);

        tvDay = (TextView) view.findViewById(R.id.tvDay);
        tvDay.setText(DateHelper.formatDayFromMilis(givenDate));

        SimpleDateFormat df = new SimpleDateFormat("E");
        btnPrevious = (Button) view.findViewById(R.id.btnPrev);
        Date previousDay = new Date(givenDate - _24_HOURS_IN_MILIS);
        btnPrevious.setText(df.format(previousDay));
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(givenDate - _24_HOURS_IN_MILIS);

                goToDay(cal);
            }
        });

        btnNext = (Button) view.findViewById(R.id.btnNext);
        Date nextDay = new Date(givenDate + _24_HOURS_IN_MILIS);
        btnNext.setText(df.format(nextDay));
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(givenDate + _24_HOURS_IN_MILIS);

                goToDay(cal);
            }
        });

       getActivity().getLoaderManager().initLoader(LOADER_RESERVATIONSFORDAY, null, this);

       adapter = new CustomCursorReservationsForDayAdapter(getActivity(), null, 0); //todo: use the correct rows put into a MatrixCursor and show this with the adapter

		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setAdapter(adapter);
		
		list.setEmptyView(view.findViewById(android.R.id.empty));
		
		return view;
	}

    private void goToDay(Calendar cal) {
        Intent intentReservationsForDay = new Intent(getActivity(), ReservationsForDayActivity.class);
        String tempMeetingRoomId = ""+getActivity().getIntent().getExtras().get(Constants.MEETINGROOM_ID);
        String tempMeetingRoomName =""+ getActivity().getIntent().getExtras().get(Constants.MEETINGROOM_NAME);
        intentReservationsForDay.putExtra(Constants.MEETINGROOM_ID, tempMeetingRoomId);
        intentReservationsForDay.putExtra(Constants.MEETINGROOM_NAME, tempMeetingRoomName);
        intentReservationsForDay.putExtra("date_to_show", cal);
        intentReservationsForDay.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intentReservationsForDay);
    }

    private Time getTodayZeroHours() {
        Time today = new Time();
        today.setToNow();
        today.hour = 0;
        today.minute = 0;
        today.second = 0;
        return today;
    }

    private Time getTimeZeroHours(Time time) {
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        return time;
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

                adapter.notifyDataSetChanged();

                return true;
            }
        });
    }

    private void delete(String meetingRoomIdString) {
        DeleteOrEditConfirmationDialogFragment fragment = DeleteOrEditConfirmationDialogFragment.newInstance(this);
        fragment.show(getActivity().getFragmentManager(), "deleteconfirmation");
    }


    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
        Log.e("test", "onlistclick");
		listener.onReservationSelected(id);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //FIXME: need all those fields?
        String[] projection = { DB.RESERVATIONS.ID, DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.personName, DB.RESERVATIONS.description};

		String selection = DB.RESERVATIONS.beginDate + "  > ? and " + DB.RESERVATIONS.beginDate + " < ?";
        

        Time now, tomorrow;
        if(isInWeekView){
            Log.e("In WeekView: ", "");
            now = new Time();
            now.set(givenDate);
            now = getTimeZeroHours(now);
            tomorrow = new Time(now);
            tomorrow.set((tomorrow.toMillis(false) + _24_HOURS_IN_MILIS));
        }else{
            now = getTodayZeroHours();
            now.set(now.toMillis(false));
            tomorrow = getTodayZeroHours();
            tomorrow.set(tomorrow.toMillis(false)+(_24_HOURS_IN_MILIS));
        }

        Log.v("start of today in millis: ", "" + new Date(now.toMillis(false)));
        Log.v("start of tomorrow in millis: ", "" + new Date(tomorrow.toMillis(false)));
        String[] selectionArgs = {""+now.toMillis(false), ""+tomorrow.toMillis(false)} ;

		CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
				ReservationsContentProvider.CONTENT_URI_RESERVATION, projection, selection,
				selectionArgs, DB.RESERVATIONS.beginDate + " ASC");

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        MatrixCursor matrixCursor = new MatrixCursor(new String[] {  DB.RESERVATIONS.personName, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.description, DB.RESERVATIONS.ID});

        long begin = 0;
        long end;
        Time tomorrow;

        if(isInWeekView){
            Time date = new Time();
            date.set(givenDate);
            end = getTimeZeroHours(date).toMillis(false); // start of the day
            tomorrow = new Time(date);
            tomorrow.set(tomorrow.toMillis(false) + _24_HOURS_IN_MILIS - 1); // 1 millisecond before the start of next day


        }else{
            end = getTodayZeroHours().toMillis(false);
            tomorrow = getTodayZeroHours();
            tomorrow.set(tomorrow.toMillis(false) +_24_HOURS_IN_MILIS - 1);
        }


        int id = 10000000;
        while (cursor.moveToNext()) {
            begin =Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)));
            if((begin-end) >= 15*60*1000){ // 15 minutes in millis


                matrixCursor.addRow(new Object[] { " ",end, begin,getResources().getString(R.string.make_reservation_here) , id++ });

            }
            matrixCursor.addRow(new Object[] {cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.personName)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.beginDate)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)),cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.description)), cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.ID))});

            end= Long.parseLong(cursor.getString(cursor.getColumnIndex(DB.RESERVATIONS.endDate)));

        }

        matrixCursor.addRow(new Object[] { " ",end, tomorrow.toMillis(false), getResources().getString(R.string.make_reservation_here) , id++ });

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
