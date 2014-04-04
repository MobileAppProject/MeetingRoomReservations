package be.vdab.project.meetingroomreservations.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import be.vdab.project.meetingroomreservations.Model.MeetingRoom;
import be.vdab.project.meetingroomreservations.Model.Reservation;
import be.vdab.project.meetingroomreservations.R;
import be.vdab.project.meetingroomreservations.db.DB;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;

public class LoadingActivity extends Activity {
	
	private Reservation[] reservations;
	private MeetingRoom[] meetingRooms;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		new DownloadTask().execute();
	}
	
	
	class DownloadTask extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... params) {
            Log.e("begin downloading","from LoadingActivity");
	        downloadReservations();
	        downloadMeetingRooms();
            Log.e("end downloading","from LoadingActivity");
	        return null;
		}

		@Override
	    protected void onPostExecute(String value) {
			//Intent intent = new Intent(getApplicationContext(), ReservationsActivity.class);
            Intent intent = new Intent(getApplicationContext(), MeetingRoomsActivity.class);
			startActivity(intent);
			finish();
	    }
		
    }

    private void downloadReservations() {
        //This gets all reservations, also inactive ones
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
            reservations = restTemplate.getForObject("http://192.168.56.1:8080/restSprintStarter/data/reservations", Reservation[].class);
            getContentResolver().delete(ReservationsContentProvider.CONTENT_URI_RESERVATION, null, null);
            if(reservations != null) {
                for (Reservation reservation : reservations) {
                    ContentValues values = new ContentValues();
                    values.put(DB.RESERVATIONS.reservationId, reservation.getReservationId());
                    values.put(DB.RESERVATIONS.meetingRoomId, reservation.getMeetingRoomId());
                    values.put(DB.RESERVATIONS.beginDate, reservation.getBeginDate());
                    values.put(DB.RESERVATIONS.endDate, reservation.getEndDate());
                    values.put(DB.RESERVATIONS.personName, reservation.getPersonName());
                    values.put(DB.RESERVATIONS.description, reservation.getDescription());
                    values.put(DB.RESERVATIONS.active, reservation.getActive());
                    getContentResolver().insert(ReservationsContentProvider.CONTENT_URI_RESERVATION, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void downloadMeetingRooms() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
            meetingRooms = restTemplate.getForObject("http://192.168.56.1:8080/restSprintStarter/data/meetingrooms", MeetingRoom[].class);
            getContentResolver().delete(ReservationsContentProvider.CONTENT_URI_MEETINGROOM, null, null);
            if(meetingRooms != null) {
                for (MeetingRoom meetingRoom : meetingRooms) {
                    ContentValues values = new ContentValues();
                    values.put(DB.MEETINGROOMS.meetingRoomId, meetingRoom.getMeetingRoomId());

                    values.put(DB.MEETINGROOMS.name, meetingRoom.getName());

                    getContentResolver().insert(ReservationsContentProvider.CONTENT_URI_MEETINGROOM, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
