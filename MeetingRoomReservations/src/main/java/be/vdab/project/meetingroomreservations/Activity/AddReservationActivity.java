package be.vdab.project.meetingroomreservations.Activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import be.vdab.project.meetingroomreservations.Constants;
import be.vdab.project.meetingroomreservations.Dialogs.DatePickerDialogFragment;
import be.vdab.project.meetingroomreservations.Dialogs.TimePickerDialogFragment;
import be.vdab.project.meetingroomreservations.Model.Reservation;
import be.vdab.project.meetingroomreservations.R;

/**
 * Created by jeansmits on 7/04/14.
 */
public class AddReservationActivity extends Activity implements DatePickerDialogFragment.Callback, LoaderManager.LoaderCallbacks<Object> {


    private static final int LOADER_RESERVATIONS = 1;

    SimpleCursorAdapter adapter;

    TextView dateView;
    TextView startView;
    TextView endView;
    TextView nameView;
    TextView descriptionView;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        getLoaderManager().initLoader(LOADER_RESERVATIONS, null, this);

        //modify this to retrieve saved username
       /* Spinner doctor = (Spinner) findViewById(R.id.add_appointment_doctor);
        String[] columns = new String[1];
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(Constants.PREFERENCES_VALUE_FIRST_NAME.equals(preferences.getString(Constants.PREFERENCES_KEY, Constants.PREFERENCES_VALUE_LAST_NAME))) {
            columns[0] = DB.DOKTERS.FIRSTNAME;
        } else {
            columns[0] = DB.DOKTERS.LASTNAME;
        }
        int[] to = new int[] { android.R.id.text1 };*/




        dateView = (TextView) findViewById(R.id.add_reservation_date);
        startView = (TextView) findViewById(R.id.add_reservation_begin_time);
        endView = (TextView) findViewById(R.id.add_reservation_end_time);
        nameView = (EditText) findViewById(R.id.add_reservation_name);
        descriptionView = (EditText) findViewById(R.id.add_reservation_description);
        saveButton = (Button) findViewById(R.id.add_reservation_save);



        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                TextView date = (TextView) findViewById(R.id.add_reservation_date);
//                TextView start = (TextView) findViewById(R.id.add_reservation_begin_time);
//                TextView end = (TextView) findViewById(R.id.add_reservation_end_time);
//                EditText name = (EditText) findViewById(R.id.add_reservation_name);
//                EditText description = (EditText) findViewById(R.id.add_reservation_description);





                new SaveTask().execute(dateView.getText().toString(),
                        startView.getText().toString(),
                        endView.getText().toString(),
                        nameView.getText().toString(),
                        descriptionView.getText().toString());
            }
        });




        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialogFragment().show(getFragmentManager(), "datepicker");
            }
        });

        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment.newInstance(startTimeListener).show(getFragmentManager(), "startpicker");
            }
        });

        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment.newInstance(endTimeListener).show(getFragmentManager(), "endpicker");
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

        Reservation[] reservations;

        @Override
        protected String doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                Reservation reservation = new Reservation();
                String beginDate = makeDateTimeString(params[0],params[1]);
                reservation.setBeginDate(beginDate);
                String endDate = makeDateTimeString(params[0], params[2]);
                reservation.setEndDate(endDate);
                reservation.setPersonName(params[2]);
                reservation.setDescription(params[3]);
                Log.e("meeting room id van den intent", getIntent().getExtras().getString(Constants.MEETINGROOM_ID));
                reservation.setMeetingRoomId(getIntent().getExtras().getString(Constants.MEETINGROOM_ID));


                restTemplate.postForObject("http://192.168.56.1:8080/restSprintStarter/data/addReservation", reservation, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String makeDateTimeString(String date, String time) {
            Log.e("date:",date);
            Log.e("time:",time);


            return date+time;
        }

        @Override
        protected void onPostExecute(String value) {
            Intent intent = new Intent(getApplicationContext(), LoadingActivity.class); // todo
            startActivity(intent);
            finish();
        }

    }
}
