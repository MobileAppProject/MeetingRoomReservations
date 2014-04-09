package be.vdab.project.meetingroomreservations.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import be.vdab.project.meetingroomreservations.Adapter.CustomCursorAdapter;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_RESERVATION;

public class ReservationFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final int LOADER_RESERVATIONS = 1;
	
	private Callbacks listener = dummyListener;
	
	private CustomCursorAdapter adapter;
	
	private View view;
	
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

        Cursor cursor =  getActivity().getContentResolver().query(reservationURI,projection, selection , selectionArgs, null );
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
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

		String selection = null;
		CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
				ReservationsContentProvider.CONTENT_URI_RESERVATION, projection, selection,
				null, null);
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

}
