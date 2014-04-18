package be.vdab.project.meetingroomreservations.Activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.DTO.ReservationDTO;
import be.vdab.project.meetingroomreservations.Dialogs.DatePickerDialogFragment;
import be.vdab.project.meetingroomreservations.Dialogs.TimePickerDialogFragment;
import be.vdab.project.meetingroomreservations.Helper.DateHelper;
import be.vdab.project.meetingroomreservations.Model.MeetingRoom;
import be.vdab.project.meetingroomreservations.R;

/**
 * Created by jeansmits on 7/04/14.
 */

//TODO: Throw warning/error when/if internet access is no longer available
public class AddReservationActivity extends Activity implements DatePickerDialogFragment.Callback, LoaderManager.LoaderCallbacks<Object> {


    private static final int LOADER_RESERVATIONS = 1;

    TextView dateView;
    TextView startView;
    TextView endView;
    TextView nameView;
    TextView descriptionView;
    Button saveButton;
    String savedMeetingRoomId;
    String savedMeetingRoomName;

    String savedReservationId;

    String mode;
    String defaultvalue;

    //variables for datepicker
    int year;
    int month;
    int day;

    //variables for timePickers
    int beginHour;
    int beginMinutes;
    int endHour;
    int endMinutes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();


        mode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mode", ""); //fixme: don't use literal strings
        defaultvalue = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("mode_input", "");
        Log.e(" preference values: ", "mode: " + mode + ", value: " + defaultvalue);

        getLoaderManager().initLoader(LOADER_RESERVATIONS, null, this);

        dateView = (TextView) findViewById(R.id.add_reservation_date);
        startView = (TextView) findViewById(R.id.add_reservation_begin_time);
        endView = (TextView) findViewById(R.id.add_reservation_end_time);
        nameView = (EditText) findViewById(R.id.add_reservation_name);
        descriptionView = (EditText) findViewById(R.id.add_reservation_description);
        saveButton = (Button) findViewById(R.id.add_reservation_save);


        if(mode.equals(Constants.USERMODE))
        {
            nameView.setText(defaultvalue);
        }
        if(b!=null)
        {
            savedMeetingRoomId = "" + b.get(Constants.MEETINGROOM_ID);
            savedMeetingRoomName = "" + b.get(Constants.MEETINGROOM_NAME);
            Log.e("savedMeetingRoomId in AddReservationActivity",savedMeetingRoomId);
            Log.e("savedMeetingRoomName in AddReservationActivity", savedMeetingRoomName);


            //for edit:
            if((b.get("beginDate")) != null){
                savedReservationId = ""+b.get("reservationId");
                String beginDate = "" + b.get("beginDate");
                String endDate = "" + b.get("endDate");
                SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd"); // todo: use datehelper
                SimpleDateFormat dfHourAndMinute = new SimpleDateFormat("HH:mm");
                Date begin = new Date(Long.parseLong(beginDate));
                GregorianCalendar beginCalendar = new GregorianCalendar();
                beginCalendar.setTime(begin);
                year = beginCalendar.get(Calendar.YEAR);
                month = beginCalendar.get(Calendar.MONTH);
                day = beginCalendar.get(Calendar.DAY_OF_MONTH);
                beginHour = beginCalendar.get(Calendar.HOUR_OF_DAY);
                beginMinutes = beginCalendar.get(Calendar.MINUTE);

                Date end = new Date(Long.parseLong(endDate));
                GregorianCalendar endCalendar = new GregorianCalendar();
                endCalendar.setTime(end);
                endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
                endMinutes = endCalendar.get(Calendar.MINUTE);

                dateView.setText(dfDate.format(begin));

                startView.setText(dfHourAndMinute.format(begin));
                endView.setText(dfHourAndMinute.format(end));
                nameView.setText("" + b.get("personName"));
                descriptionView.setText("" + b.get("description"));
            }

        }
        else{
            Log.e("bundle extras is null", "");
        }

        setTitle(savedMeetingRoomName);


        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (savedMeetingRoomId != null && !savedMeetingRoomId.equals("")) {
                    if(year == 0) {


                        new SaveTask().execute(dateView.getText().toString(),
                                startView.getText().toString(),
                                endView.getText().toString(),
                                nameView.getText().toString(),
                                descriptionView.getText().toString());
                    }
                    else{
                        new EditTask().execute(dateView.getText().toString(),
                                startView.getText().toString(),
                                endView.getText().toString(),
                                nameView.getText().toString(),
                                descriptionView.getText().toString());
                    }
                }
                else{
                    Log.e("meetingroom id is null", "");
                }
            }

        });




        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dateView.getText().equals(""))
                {
                    Calendar cal = new GregorianCalendar();
                    try {
                       cal.setTime( DateHelper.ParseDateFromString(dateView.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    new DatePickerDialogFragment(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datepicker");
                }else
                {
                    new DatePickerDialogFragment(year,month,day).show(getFragmentManager(), "datepicker");
                }
            }
        });

        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!startView.getText().equals("")){
                    Calendar cal = new GregorianCalendar();
                    try {
                        cal.setTime( DateHelper.ParseTimeFromString(startView.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TimePickerDialogFragment.newInstance(startTimeListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)).show(getFragmentManager(), "startpicker");
                }else{
                    TimePickerDialogFragment.newInstance(startTimeListener, beginHour, beginMinutes).show(getFragmentManager(), "startpicker");
                }
            }
        });

        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!endView.getText().equals("")){
                    Calendar cal = new GregorianCalendar();
                    try {
                        cal.setTime( DateHelper.ParseTimeFromString(endView.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TimePickerDialogFragment.newInstance(endTimeListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)).show(getFragmentManager(), "endpicker");
                }else{
                    TimePickerDialogFragment.newInstance(endTimeListener, endHour, endMinutes).show(getFragmentManager(), "endpicker");
                }

            }
        });

    }

    @Override
    public void onDateSelected(String date) {
       dateView.setText(date);
    }

    TimePickerDialogFragment.Callback startTimeListener = new TimePickerDialogFragment.Callback() {
        @Override
        public void onTimeSelected(String time) {
            startView.setText(time);
        }
    };

    TimePickerDialogFragment.Callback endTimeListener = new TimePickerDialogFragment.Callback() {
        @Override
        public void onTimeSelected(String time) {
            endView.setText(time);
        }
    };

    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> objectLoader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader<Object> objectLoader) {

    }

    class SaveTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                MeetingRoom meetingRoom = new MeetingRoom();
                meetingRoom.setMeetingRoomId(savedMeetingRoomId);
                meetingRoom.setName(savedMeetingRoomName);

                ReservationDTO rezzy = new ReservationDTO();
                rezzy.setMeetingRoom(meetingRoom);
                String beginDate = makeDateTimeString(params[0],params[1]);
                rezzy.setBeginDate(beginDate);
                String endDate = makeDateTimeString(params[0], params[2]);
                rezzy.setEndDate(endDate);
                rezzy.setDescription(params[4]);
                rezzy.setPersonName(params[3]);

                //TODO: Still throwing MalformedJsonException, however it works
                restTemplate.postForObject("http://192.168.56.1:8080/restSprintStarter/data/reservations/addReservation", rezzy, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        private String makeDateTimeString(String date, String time) {
            Log.e("date:",date);
            Log.e("time:",time);
            Log.e("timezone", TimeZone.getDefault().toString());

            return date+"T"+time+":00+0200";
        }

        @Override
        protected void onPostExecute(String value) {
            Intent intent = new Intent(getApplicationContext(), LoadingActivity.class); // todo: currently goes to loadingactivity -> meetingrooms. Better to go to the reservations directly and update with DataRefreshService
            startActivity(intent);
            finish();
        }

    }

        class EditTask extends AsyncTask<String, Integer, String> {

            @Override
            protected String doInBackground(String... params) {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                    MeetingRoom meetingRoom = new MeetingRoom();
                    meetingRoom.setMeetingRoomId(savedMeetingRoomId);
                    meetingRoom.setName(savedMeetingRoomName);

                    ReservationDTO rezzy = new ReservationDTO();
                    rezzy.setMeetingRoom(meetingRoom);
                    String beginDate = makeDateTimeString(params[0],params[1]);

                    rezzy.setBeginDate(beginDate);
                    String endDate = makeDateTimeString(params[0], params[2]);
                    rezzy.setEndDate(endDate);
                    rezzy.setDescription(params[4]);
                    rezzy.setPersonName(params[3]);

                    //fixme: Still throwing MalformedJsonException, however it works
                    restTemplate.put("http://192.168.56.1:8080/restSprintStarter/data/reservations/updateReservation/" + savedReservationId, rezzy, String.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        private String makeDateTimeString(String date, String time) {
            Log.e("date:",date);
            Log.e("time:",time);
            Log.e("timezone", TimeZone.getDefault().toString());

            return date+"T"+time+":00.000Z";
        }

        @Override
        protected void onPostExecute(String value) {
            Intent intent = new Intent(getApplicationContext(), LoadingActivity.class); // todo: currently goes to loadingactivity -> meetingrooms. Better to go to the reservations directly and update with DataRefreshService
            startActivity(intent);
            finish();
        }

    }
}
