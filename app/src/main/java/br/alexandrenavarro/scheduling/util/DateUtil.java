package br.alexandrenavarro.scheduling.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alexandrenavarro on 03/09/17.
 */

public class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy HH:mm";

    public static Calendar getNextBusinessDay(){
        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            calendar.add(Calendar.DAY_OF_WEEK, 2);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendar.get(Calendar.HOUR_OF_DAY) > 18){
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        return calendar;
    }

    public static Date parseDate(String source){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        try {
            return dateFormat.parse(source);
        }catch (Exception e){}

        return null;
    }
}