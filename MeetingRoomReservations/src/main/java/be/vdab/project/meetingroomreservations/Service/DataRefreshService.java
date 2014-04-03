package be.vdab.project.meetingroomreservations.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import be.vdab.project.meetingroomreservations.Model.MeetingRoom;
import be.vdab.project.meetingroomreservations.Model.Reservation;
import be.vdab.project.meetingroomreservations.db.ReservationsContentProvider;
import be.vdab.project.meetingroomreservations.db.DB;

/**
 * Created by jeansmits on 02/04/14.
 */
public class DataRefreshService extends IntentService {
    private static String TAG = "DataRefreshService";

    public DataRefreshService() {
        super(TAG);
    }
    public DataRefreshService(String name) {
        super(name);
    }

    @Override

    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "started downloading data");
        downloadReservations();
        downloadMeetingRooms();
        Log.e(TAG, "finished downloading data");
    }

    private void downloadReservations() {
        //This gets all reservations, also inactive ones
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
            Reservation[] reservations = restTemplate.getForObject("http://192.168.56.1:8080/restSprintStarter/data/reservations", Reservation[].class);
            getContentResolver().delete(ReservationsContentProvider.CONTENT_URI_RESERVATION, null, null);
            if(reservations != null) {
                for (Reservation reservation : reservations) {
                    ContentValues values = new ContentValues();
                    values.put(DB.RESERVATIONS.reservationId, reservation.getId());
                    values.put(DB.RESERVATIONS.beginDate, reservation.getBeginDate());
                    values.put(DB.RESERVATIONS.endDate, reservation.getEndDate());
                    //values.put(DB.RESERVATIONS.DATE, afspraak.getDatum());
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
            MeetingRoom[] meetingRooms = restTemplate.getForObject("http://192.168.56.1:8080/restSprintStarter/data/meetingRooms", MeetingRoom[].class);
            getContentResolver().delete(ReservationsContentProvider.CONTENT_URI_MEETINGROOM, null, null);
            if(meetingRooms != null) {
                for (MeetingRoom meetingRoom : meetingRooms) {
                    ContentValues values = new ContentValues();
                    values.put(DB.MEETINGROOMS.meetingRoomId, meetingRoom.getId());

                    values.put(DB.MEETINGROOMS.name, meetingRoom.getName());

                    getContentResolver().insert(ReservationsContentProvider.CONTENT_URI_MEETINGROOM, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
