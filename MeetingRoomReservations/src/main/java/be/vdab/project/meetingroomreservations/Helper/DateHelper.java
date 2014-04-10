package be.vdab.project.meetingroomreservations.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeansmits on 10/04/14.
 */
public class DateHelper {

    public static String formatDayFromMilis(long dateInMilis){

        SimpleDateFormat df = new SimpleDateFormat("E dd/MM/yyyy");
        Date day = new Date(dateInMilis);
        return df.format(day);
    }

    public static String formatHoursMinutesFromMilis(long dateInMilis){

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date hours = new Date(dateInMilis);
        return df.format(hours);
    }

}

