package be.vdab.project.meetingroomreservations.Activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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
import be.vdab.project.meetingroomreservations.DTO.ReservationDTO;
import be.vdab.project.meetingroomreservations.Dialogs.DatePickerDialogFragment;
import be.vdab.project.meetingroomreservations.Dialogs.TimePickerDialogFragment;
import be.vdab.project.meetingroomreservations.Model.MeetingRoom;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;

import static be.vdab.project.meetingroomreservations.db.ReservationsContentProvider.CONTENT_URI_MEETINGROOM;

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
    String savedMeetingRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);


        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            savedMeetingRoomId = "" + b.get(Constants.MEETINGROOM_ID);
            Log.e("savedMeetingRoomId in AddReservationActivity",savedMeetingRoomId);

        }
        else{
            Log.e("bundle extras is null", "sdfsdf");
        }



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


                if (savedMeetingRoomId != null && !savedMeetingRoomId.equals("")) {


                    new SaveTask().execute(dateView.getText().toString(),
                            startView.getText().toString(),
                            endView.getText().toString(),
                            nameView.getText().toString(),
                            descriptionView.getText().toString());
                }
                else{
                    Log.e("meetingroom id is null", "");
                }
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

        be.vdab.project.meetingroomreservations.Model.Reservation[] reservations;

        @Override
        protected String doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());


                Uri meetingRoomURI = CONTENT_URI_MEETINGROOM;
                String[] projection = { DB.MEETINGROOMS.meetingRoomId,DB.MEETINGROOMS.name };
                String selection = DB.MEETINGROOMS.ID + "  = ?";
                Log.e("testestestestestest", "testest");
                String[] selectionArgs  = { savedMeetingRoomId};

                Cursor cursor =  getContentResolver().query(meetingRoomURI, projection, selection, selectionArgs, null);
                if(getContentResolver() == null){
                    Log.e("waaah","contentresolver is null");
                }
                cursor.moveToFirst();
                int indexName = cursor.getColumnIndex(DB.MEETINGROOMS.name);
                int indexID = cursor.getColumnIndex(DB.MEETINGROOMS.meetingRoomId);



                String name;
                String id;
                //TODO: stop using dummy data and fix this issue
                if(cursor.moveToFirst()){
                    name = cursor.getString(indexName);
                    id = cursor.getString(indexID);
                    cursor.close();
                }
                else{
                    Log.e("Personalized error: ", "Problem with cursor, will not retrieve the required data but filled variables with dummy data! Problem occured in the AddReservationActivity class.");
                    name="RudyRoom";
                    id="1";
                }

                MeetingRoom meetingRoom = new MeetingRoom();
                meetingRoom.setMeetingRoomId(id);
                meetingRoom.setName(name);



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


            return date+"T"+time+":00.000Z";
        }

        @Override
        protected void onPostExecute(String value) {
            Intent intent = new Intent(getApplicationContext(), LoadingActivity.class); // todo
            startActivity(intent);
            finish();
        }

    }
}
