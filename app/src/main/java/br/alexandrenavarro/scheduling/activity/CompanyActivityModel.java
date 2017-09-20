package br.alexandrenavarro.scheduling.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.alexandrenavarro.scheduling.model.Scheduling;
import br.alexandrenavarro.scheduling.util.DateUtil;

import static br.alexandrenavarro.scheduling.util.FormatIdUtil.geIdWithFormattedDateWithoutHours;

/**
 * Created by alexandrenavarro on 9/20/17.
 */

public class CompanyActivityModel extends ViewModel {

    private MutableLiveData<Map<Long, Set<Integer>>> data;
    protected DatabaseReference mSchedulingRef;

    public CompanyActivityModel() {
        mSchedulingRef = FirebaseDatabase.getInstance().getReference().
                child("professionalSchedule");
    }

    public MutableLiveData<Map<Long, Set<Integer>>> loadScheduling(final long companyId) {
        if (data == null) {
            data = new MutableLiveData<>();
            mSchedulingRef.orderByChild("idCompany_day").
                    equalTo(geIdWithFormattedDateWithoutHours(DateUtil.getNextBusinessDay(), String.valueOf(companyId))).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SimpleDateFormat dateFormatRequest = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                            HashMap<Long, Set<Integer>> map = new HashMap<>();
                            for (DataSnapshot schedulingSnapshot : dataSnapshot.getChildren()) {
                                Scheduling scheduling = schedulingSnapshot.getValue(Scheduling.class);
                                Date date = null;
                                try {
                                    date = dateFormatRequest.parse(scheduling.getDate());
                                } catch (ParseException e) {
                                    Log.d(CompanyActivity.class.getSimpleName(), e.getMessage());
                                }

                                if (date != null) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);

                                    if (!map.containsKey(scheduling.getIdProfessional())) {
                                        map.put(scheduling.getIdProfessional(), new TreeSet<Integer>());
                                    }

                                    map.get(scheduling.getIdProfessional()).add(calendar.get(Calendar.HOUR_OF_DAY));
                                }
                            }

                            data.setValue(map);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(CompanyActivity.class.getSimpleName(), databaseError.getMessage());
                        }
                    });
        }

        return data;
    }
}
