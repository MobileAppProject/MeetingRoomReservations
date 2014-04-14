package be.vdab.project.meetingroomreservations.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeansmits on 10/04/14.
 */
public class DateHelper { // Time > Calendar > Date

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

    /**
     *
     * @param dateString: in yyyy-MM-dd format
     * @throws ParseException
     * @return Date with given string as date
     */
    public static Date ParseDateFromString(String dateString) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.parse(dateString);
    }

    /**
     *
     * @param dateString: in HH:mm format
     * @return Date with given string as time
     * @throws ParseException
     */
    public static Date ParseTimeFromString(String dateString) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        return df.parse(dateString);
    }

}

