package br.alexandrenavarro.scheduling.util;

import java.util.ArrayList;
import java.util.List;

import br.alexandrenavarro.scheduling.model.Hour;

/**
 * Created by alexandrenavarro on 02/09/17.
 */

public class WorkHoursUtils {

    public static List<Hour> generate(){
        List<Hour> list = new ArrayList<>();

        list.add(new Hour(9, true));
        list.add(new Hour(10, true));
        list.add(new Hour(11, true));
        list.add(new Hour(13, true));
        list.add(new Hour(14, true));
        list.add(new Hour(15, true));
        list.add(new Hour(16, true));
        list.add(new Hour(17, true));

        return list;
    }
}