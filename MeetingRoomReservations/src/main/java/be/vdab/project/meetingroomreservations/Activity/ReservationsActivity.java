package be.vdab.project.meetingroomreservations.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Fragment.ReservationFragment;
import be.vdab.project.meetingroomreservations.R;

public class ReservationsActivity extends Activity implements ReservationFragment.Callbacks{

	private boolean dualPaneMode;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_reservations);


	}


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reservations, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_reservation:
			Intent intent = new Intent(getApplicationContext(), AddReservationActivity.class);
            String tempId = ""+getIntent().getExtras().get(Constants.MEETINGROOM_ID);
            String tempName =""+ getIntent().getExtras().get(Constants.MEETINGROOM_NAME);
            Log.e("MeetingRoomActivity in ReservationsActivity: ",  "string so it's not null: " + tempId);
            intent.putExtra(Constants.MEETINGROOM_ID, tempId);
            intent.putExtra(Constants.MEETINGROOM_NAME, tempName);
			startActivity(intent);
			return true;

        case R.id.action_show_reservations_for_day:
            Intent intentReservationsForDay = new Intent(getApplicationContext(), ReservationsForDayActivity.class);
            String tempMeetingRoomId = ""+getIntent().getExtras().get(Constants.MEETINGROOM_ID);
            String tempMeetingRoomName =""+ getIntent().getExtras().get(Constants.MEETINGROOM_NAME);
            Log.e("MeetingRoomActivity in ReservationsActivity: ",  "string so it's not null: " + tempMeetingRoomId);
            intentReservationsForDay.putExtra(Constants.MEETINGROOM_ID, tempMeetingRoomId);
            intentReservationsForDay.putExtra(Constants.MEETINGROOM_NAME, tempMeetingRoomName);
            startActivity(intentReservationsForDay);
            return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onReservationSelected(Long id) {
//		if(dualPaneMode) {
//			Bundle arguments = new Bundle();
//			arguments.putLong(Constants.APPOINTMENT_ID, id);
//			AppointmentDetailFragment fragment = new AppointmentDetailFragment();
//			fragment.setArguments(arguments);
//			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.appointment_detail_container, fragment);
//			transaction.commit();
//		} else {
        Log.e("ReservationsActivity", "onreservationSelected");
        Intent intent = new Intent(getApplicationContext(), ReservationDetailActivity.class);
        intent.putExtra(Constants.RESERVATION_ID, id.intValue()); // todo: send whole Reservation?
        startActivity(intent);

		
	}

}
