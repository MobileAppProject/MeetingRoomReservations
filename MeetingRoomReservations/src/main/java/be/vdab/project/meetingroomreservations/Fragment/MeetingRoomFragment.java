package be.vdab.project.meetingroomreservations.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.Service.DataRefreshService;
import be.vdab.project.meetingroomreservations.db.DB;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_MEETINGROOM;

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
				listener.onMeetingRoomSelected(id, "");
			}
		});

		return view;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

        Intent refreshIntent = new Intent(getActivity(), DataRefreshService.class);

        //query arguments
        Uri meetingRoomURI = CONTENT_URI_MEETINGROOM;
        String[] projection = { DB.MEETINGROOMS.meetingRoomId, DB.MEETINGROOMS.name };
        String selection = DB.MEETINGROOMS.ID + "  = ?";
        String[] selectionArgs  = { Long.toString(id)  };

        Cursor cursor =  getActivity().getContentResolver().query(meetingRoomURI,projection, selection , selectionArgs, null );
        cursor.moveToFirst();

        int index = cursor.getColumnIndex(DB.MEETINGROOMS.meetingRoomId);
        //Log.e("TAG", "value from cursor column " + index + ": " + cursor.getString(index)); // error on this log ???
        int indexName = cursor.getColumnIndex(DB.MEETINGROOMS.name);
        String meetingRoomIdString = cursor.getString(index); // fixme: CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0, happens only sometimes
        String meetingRoomNameString = cursor.getString(indexName);

        refreshIntent.putExtra("meetingRoomId", meetingRoomIdString);

        cursor.close(); //fixme: no clue if we need to do this??
        getActivity().startService(refreshIntent);

		listener.onMeetingRoomSelected(Long.parseLong(meetingRoomIdString), meetingRoomNameString);
	}
	
	/*public void setActivateOnItemClick(boolean activateOnItemClick) {
		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}*/

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { DB.MEETINGROOMS.ID, DB.MEETINGROOMS.meetingRoomId,DB.MEETINGROOMS.name};
		
		String selection = null;
		CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),
				CONTENT_URI_MEETINGROOM, projection, selection,
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
		public void onMeetingRoomSelected(Long id, String meetingRoomName);
	}

	private static Callbacks dummyListener = new Callbacks() {
		@Override
		public void onMeetingRoomSelected(Long id, String meetingRoomName) {
		}
	};

}
