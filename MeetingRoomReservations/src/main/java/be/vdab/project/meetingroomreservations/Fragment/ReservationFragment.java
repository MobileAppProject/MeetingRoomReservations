package be.vdab.project.meetingroomreservations.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import be.vdab.project.meetingroomreservations.Adapter.CustomCursorAdapter;
import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Dialogs.DeleteOrEditConfirmationDialogFragment;
import be.vdab.project.meetingroomreservations.Model.Reservation;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_RESERVATION;

public class ReservationFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, DeleteOrEditConfirmationDialogFragment.Callback {

	private static final int LOADER_RESERVATIONS = 1;
	
	private Callbacks listener = dummyListener;
	
	private CustomCursorAdapter adapter;
	
	private View view;

    private String reservationIdString;
    private Reservation res;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_reservations, container, false);

        getLoaderManager().initLoader(LOADER_RESERVATIONS, null, this);

//		String[] columns = { DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.beginDate }; // from
//		int[] items = { R.id.meetingRoom, R.id.beginDate}; // to

        //query arguments
        Uri reservationURI = CONTENT_URI_RESERVATION;
        String[] projection = { DB.RESERVATIONS.personName, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.ID };
        String selection = null;
        String[] selectionArgs  = null;

        Cursor cursor =  getActivity().getContentResolver().query(reservationURI,projection, selection , selectionArgs, "reservations.personName ASC" );
        cursor.moveToFirst();

		//adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.reservations_list_item,null,columns,items,0);
       adapter = new CustomCursorAdapter(getActivity(), cursor, 0);

		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setAdapter(adapter);
		
		list.setEmptyView(view.findViewById(android.R.id.empty));
		
//		list.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position,
//					long id) {
//				listener.onReservationSelected(id);
//			}
//		});
		
		return view;
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
        Log.e("test", "onlistclick");   // FIXME: DEZE LOG WRDT NIET UITGEVOERT
		listener.onReservationSelected(id);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //FIXME: need all those fields?
        String[] projection = { DB.RESERVATIONS.ID, DB.RESERVATIONS.meetingRoomId, DB.RESERVATIONS.beginDate, DB.RESERVATIONS.endDate, DB.RESERVATIONS.personName, DB.RESERVATIONS.description};

		String selection = DB.RESERVATIONS.endDate + " > ?";
        Time now = new Time();
        now.setToNow();
        String[] selectionArgs = {String.valueOf(now.toMillis(false))};
		CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
				ReservationsContentProvider.CONTENT_URI_RESERVATION, projection, selection,
				selectionArgs, DB.RESERVATIONS.beginDate + " ASC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
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
