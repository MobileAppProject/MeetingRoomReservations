package be.vdab.project.meetingroomreservations.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Fragment.MeetingRoomFragment;
import be.vdab.project.meetingroomreservations.R;

public class MeetingRoomsActivity extends Activity implements MeetingRoomFragment.Callbacks{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meetingrooms);
		
		/*if (findViewById(R.id.reservation_detail_container) != null) {
			dualPaneMode = true;

			// In dual-pane mode worden items 'geactiveerd' wanneer we er op klikken
			((ReservationFragment) getFragmentManager().findFragmentById(
					R.id.appointment_list)).setActivateOnItemClick(true);
		}*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onMeetingRoomSelected(Long id, String meetingRoomName) {
        Log.e("TESTING PREFERENCES", "pref: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mode", ""));
		/*if(dualPaneMode) {
			Bundle arguments = new Bundle();
			arguments.putLong(Constants.APPOINTMENT_ID, id);
			AppointmentDetailFragment fragment = new AppointmentDetailFragment();
			fragment.setArguments(arguments);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.appointment_detail_container, fragment);
			transaction.commit();
		} else {*/
			Intent intent = new Intent(getApplicationContext(), ReservationsActivity.class);
			intent.putExtra(Constants.MEETINGROOM_ID, id);
            intent.putExtra(Constants.MEETINGROOM_NAME, meetingRoomName);
            Log.e("MeetingRoomActivity onMeetingRoomSelected: ", Long.toString(id) + ", " + meetingRoomName);
			startActivity(intent);
		}
		
	//}

}
