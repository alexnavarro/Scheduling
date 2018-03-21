package br.alexandrenavarro.scheduling.util;

import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.alexandrenavarro.scheduling.model.Hour;

/**
 * Created by alexandrenavarro on 02/09/17.
 */

public class WorkHoursUtils {

    private static final int hours[] = new int[]{9, 10, 11, 13, 14, 15, 16, 17};

    public static List<Hour> generate(Calendar calendar){
        List<Hour> list = new ArrayList<>();

        boolean today = DateUtils.isToday(calendar.getTimeInMillis());
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        for(int hour : hours){
            if(today && currentHour > hour){
                list.add(new Hour(hour, false));
            } else {
                list.add(new Hour(hour, true));
            }
        }

        return list;
    }
}