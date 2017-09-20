package br.alexandrenavarro.scheduling.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.alexandrenavarro.scheduling.model.Hour;
import br.alexandrenavarro.scheduling.model.Professional;
import br.alexandrenavarro.scheduling.model.Scheduling;
import br.alexandrenavarro.scheduling.util.WorkHoursUtils;

import static br.alexandrenavarro.scheduling.util.FormatIdUtil.geIdWithFormattedDateWithoutHours;

/**
 * Created by alexandrenavarro on 9/20/17.
 */

public class SchedulingActivityViewModel extends ViewModel {

    private DatabaseReference mDatabase;
    private List<Hour> availableHours = WorkHoursUtils.generate(Calendar.getInstance());
    private MutableLiveData<List<Hour>> data;


    public SchedulingActivityViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public MutableLiveData<List<Hour>> loadScheduling(final Calendar calendar, long idProfessional) {
        if (data == null) {
            data = new MutableLiveData<>();
            loadingScheduling(calendar, idProfessional);
        }

        return data;
    }

    public void loadingScheduling(final Calendar calendar, long idProfessional) {
        final SimpleDateFormat dateFormatRequest = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        mDatabase.child("professionalSchedule").orderByChild("idProfessionalDay").
                equalTo(geIdWithFormattedDateWithoutHours(calendar,
                        String.valueOf(idProfessional))).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (Hour hour : availableHours) {
                            hour.setAvailable(true);
                        }

                        for (DataSnapshot schedulingSnapshot : dataSnapshot.getChildren()) {
                            Scheduling scheduling = schedulingSnapshot.getValue(Scheduling.class);

                            Date date = null;
                            try {
                                date = dateFormatRequest.parse(scheduling.getDate());
                            } catch (ParseException e) {

                                Log.d(CompanyActivity.class.getSimpleName(), e.getMessage());
                            }

                            Calendar instance = Calendar.getInstance();
                            instance.setTime(date);

                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                            if (!format.format(calendar.getTime()).equals(format.format(instance.getTime()))) {
                                continue;
                            }

                            Hour hour = new Hour(instance.get(Calendar.HOUR_OF_DAY), true);
                            if (availableHours.contains(hour)) {
                                int index = availableHours.indexOf(hour);
                                availableHours.get(index).setAvailable(false);
                            }
                        }

                        SchedulingActivityViewModel.this.data.setValue(availableHours);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void pickupHour(final Calendar calendar, final Professional professional, final FirebaseUser currentUser, final String dateWithHour) {
        mDatabase.child("professionalSchedule").orderByChild("idUserProfessionalDay").
                equalTo(geIdWithFormattedDateWithoutHours(calendar, currentUser.getUid(),
                        String.valueOf(professional.getId())))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }

                        Scheduling scheduling = new Scheduling();
                        scheduling.setDate(dateWithHour);
                        scheduling.setIdCompany(professional.getIdCompany());
                        scheduling.setIdProfessional(professional.getId());
                        scheduling.setName(currentUser.getDisplayName());
                        scheduling.setUid(currentUser.getUid());
                        scheduling.setUserEmail(currentUser.getEmail());
                        scheduling.setUserPhone(currentUser.getPhoneNumber());
                        scheduling.setProfessionalName(professional.getName());
                        scheduling.setSpecialization(professional.getSpecialization());

                        scheduling.setIdCompany_day(
                                geIdWithFormattedDateWithoutHours(calendar, String.valueOf(professional.getIdCompany())));//"10_04-09-2017"
                        scheduling.setIdUserProfessionalDay(
                                geIdWithFormattedDateWithoutHours(calendar, currentUser.getUid(), String.valueOf(professional.getId())));
                        scheduling.setIdProfessionalDay(
                                geIdWithFormattedDateWithoutHours(calendar, String.valueOf(professional.getId())));

                        scheduling.setIdUserDay(geIdWithFormattedDateWithoutHours(calendar, currentUser.getUid()));

                        String key = mDatabase.child("professionalSchedule").push().getKey();
                        mDatabase.child("professionalSchedule").child(key).setValue(scheduling);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(SchedulingActivity.class.getSimpleName(), "onCancelled", databaseError.toException());

                    }
                });
    }
}
