package br.alexandrenavarro.scheduling.util;

import java.util.Calendar;

/**
 * Created by alexandrenavarro on 03/09/17.
 */

public class DateUtil {

    public static Calendar getNextBusinessDay(){
        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            calendar.add(Calendar.DAY_OF_WEEK, 2);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        return calendar;
    }
}
