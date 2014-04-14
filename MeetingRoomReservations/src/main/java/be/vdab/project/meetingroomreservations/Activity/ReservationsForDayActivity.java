package be.vdab.project.meetingroomreservations.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Fragment.ReservationsForDayFragment;
import be.vdab.project.meetingroomreservations.R;

public class ReservationsForDayActivity extends FragmentActivity implements ReservationsForDayFragment.Callbacks{

    private static final String TAG = "ReservationsForDayActivity";
    private Calendar cal;

    private int currentId;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations_for_day);

        // todo: currentId = getIntent().getIntExtra(, 0);

        viewPager = (ViewPager) findViewById(R.id.reservation_for_day_viewpager);
        viewPager.setAdapter(new ReservationsForDayPagerAdapter(getSupportFragmentManager()));

        cal = (Calendar) getIntent().getExtras().get("date_to_show");

        Log.e(TAG, "oncreate - viewpager: " + viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reservations_for_day, menu);
        return true;
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

            case R.id.action_show_reservations_for_month:
                Intent intentReservationsForMonth = new Intent(getApplicationContext(), ExtendedCalendarActivity.class);
                String tempMeetingRoomId2 = ""+getIntent().getExtras().get(Constants.MEETINGROOM_ID);
                String tempMeetingRoomName2 =""+ getIntent().getExtras().get(Constants.MEETINGROOM_NAME);
                Log.e("MeetingRoomActivity in ReservationsActivity: ",  "string so it's not null: " + tempMeetingRoomId2);
                intentReservationsForMonth.putExtra(Constants.MEETINGROOM_ID, tempMeetingRoomId2);
                intentReservationsForMonth.putExtra(Constants.MEETINGROOM_NAME, tempMeetingRoomName2);
                startActivity(intentReservationsForMonth);
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
        Log.e("ReservationsForDayActivity", "onreservationSelected");
        if(id<9999999) { // fixme: please make this better
            Intent intent = new Intent(getApplicationContext(), ReservationDetailActivity.class);
            intent.putExtra(Constants.RESERVATION_ID, id.intValue()); // todo: send whole Reservation?
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(getApplicationContext(), AddReservationActivity.class);
            String tempId = ""+getIntent().getExtras().get(Constants.MEETINGROOM_ID);
            String tempName =""+ getIntent().getExtras().get(Constants.MEETINGROOM_NAME);
           // Log.e("MeetingRoomActivity in ReservationsActivity: ",  "string so it's not null: " + tempId);
            intent.putExtra(Constants.MEETINGROOM_ID, tempId);
            intent.putExtra(Constants.MEETINGROOM_NAME, tempName);
            startActivity(intent);
        }


    }

    private class ReservationsForDayPagerAdapter extends FragmentStatePagerAdapter {


        public ReservationsForDayPagerAdapter(FragmentManager fm ) {
            super(fm);

        }


        @Override
        public Fragment getItem(int i) {
            Bundle arguments = new Bundle();
            Log.e("getItem: ", "" + i);


            if(getIntent().hasExtra("date_to_show")){
                arguments.putLong(Constants.DATE, ((Calendar)getIntent().getExtras().get("date_to_show")).getTimeInMillis());
                arguments.putBoolean(Constants.WEEKVIEW, true);
            }else{
                arguments.putLong(Constants.DATE, getDateWeekday(i+2));
                arguments.putBoolean(Constants.WEEKVIEW, true);
            }

            ReservationsForDayFragment fragment = new ReservationsForDayFragment();
            fragment.setArguments(arguments);



            Log.e("TESTING GETDATEWEEKDAY: ", "day 1: " + getDateWeekday(2) + "as Date: " + (new Date(getDateWeekday(2))));
            Log.e("TESTING GETDATEWEEKDAY: ", "day 2: " + getDateWeekday(3) + "as Date: " + (new Date(getDateWeekday(3))));
            Log.e("TESTING GETDATEWEEKDAY: ", "day 3: " + getDateWeekday(4) + "as Date: " + (new Date(getDateWeekday(4))));
            Log.e("TESTING GETDATEWEEKDAY: ", "day 4: " + getDateWeekday(5) + "as Date: " + (new Date(getDateWeekday(5))));
            Log.e("TESTING GETDATEWEEKDAY: ", "day 5: " + getDateWeekday(6) + "as Date: " + (new Date(getDateWeekday(6))));
            return fragment;
        }

        @Override
        public int getCount() {
            return 1; //there are 5 weekdays. I doubt this will change any time soon.
        }

        private Long getDateWeekday(int dayOfWeek){
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

            return calendar.getTimeInMillis();
        }
    }

}
