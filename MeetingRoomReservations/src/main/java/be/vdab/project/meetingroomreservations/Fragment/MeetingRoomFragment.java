package be.vdab.project.meetingroomreservations.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

public class MeetingRoomFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final int LOADER_MEETINGROOMS = 0;
	
	private Callbacks listener = dummyListener;
	
	private SimpleCursorAdapter adapter;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_meetingrooms, container, false);
		
		getLoaderManager().initLoader(LOADER_MEETINGROOMS, null, this);
		
//		String[] columns = { DB.MEETINGROOMS.ID, DB.MEETINGROOMS.name };
//		int[] items = { R.id.meetingRoom, R.id.beginDate};
        String[] columns = {DB.MEETINGROOMS.name}; // from
        int[] items = { R.id.meetingRoomName}; // to


       
		adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),R.layout.meetingrooms_list_item,null,columns,items,0);
        ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setAdapter(adapter);
		
		list.setEmptyView(view.findViewById(android.R.id.empty));
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				listener.onMeetingRoomSelected(id);
			}
		});

		return view;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		listener.onMeetingRoomSelected(id);
	}
	
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { DB.MEETINGROOMS.ID, DB.MEETINGROOMS.meetingRoomId,DB.MEETINGROOMS.name};
		
		String selection = null;
		CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
				ReservationsContentProvider.CONTENT_URI_MEETINGROOM, projection, selection,
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
		public void onMeetingRoomSelected(Long id);
	}

	private static Callbacks dummyListener = new Callbacks() {
		@Override
		public void onMeetingRoomSelected(Long id) {
		}
	};

}
