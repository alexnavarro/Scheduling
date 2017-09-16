package br.alexandrenavarro.scheduling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

import br.alexandrenavarro.scheduling.OnDateChange;
import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.adapter.SchedulingAdapter;
import br.alexandrenavarro.scheduling.fragment.DatePickerFragment;
import br.alexandrenavarro.scheduling.holder.OnItemClickListener;
import br.alexandrenavarro.scheduling.model.Hour;
import br.alexandrenavarro.scheduling.model.Professional;
import br.alexandrenavarro.scheduling.model.Scheduling;
import br.alexandrenavarro.scheduling.util.DateUtil;
import br.alexandrenavarro.scheduling.util.WorkHoursUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.alexandrenavarro.scheduling.activity.MainActivity.RC_SIGN_IN;
import static br.alexandrenavarro.scheduling.util.FormatIdUtil.geIdWithFormattedDateWithoutHours;

/**
 * Created by alexandrenavarro on 29/08/17.
 */

public class SchedulingActivity extends AppCompatActivity implements OnDateChange, OnItemClickListener {

    public static final String EXTRA_PROFESSIONAL = "Professional";

    @BindView(R.id.txt_description) TextView mTextDescription;
    @BindView(R.id.et_date_time) EditText mEtDateTime;
    @BindView(R.id.recycler_view_available_time)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mManager;

    private Professional mProfessional;
    private List<Hour> availableHours = WorkHoursUtils.generate(Calendar.getInstance());
    private SchedulingAdapter mAdapter;
    private int year;
    private int month;
    private int day;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_activity);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (savedInstanceState == null) {
            mProfessional = getIntent().getParcelableExtra(EXTRA_PROFESSIONAL);
        }

        bind();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            mAuth.getCurrentUser().reload();
        }
    }

    @OnClick(R.id.et_date_time)
    public void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void bind() {
        if (mProfessional != null) {
            mTextDescription.setText(mProfessional.getSpecialization());
            setTitle(mProfessional.getName());
        }

        Calendar calendar = DateUtil.getNextBusinessDay();
        mEtDateTime.setText(new SimpleDateFormat("dd/MM/yyyy").
                format(calendar.getTime()));

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mAdapter = new SchedulingAdapter(availableHours, this);
        mRecyclerView.setAdapter(mAdapter);
        loadScheduling( DateUtil.getNextBusinessDay());

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void loadScheduling(Calendar calendar) {
        SimpleDateFormat dateFormatRequest = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        mDatabase.child("professionalSchedule").orderByChild("idProfessionalDay").
                equalTo(geIdWithFormattedDateWithoutHours(calendar,
                        String.valueOf(mProfessional.getId()))).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (Hour hour : availableHours){
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

                            if(!format.format(calendar.getTime()).equals(format.format(instance.getTime()))){
                                continue;
                            }

                            Hour hour = new Hour(instance.get(Calendar.HOUR_OF_DAY), true);
                            if (availableHours.contains(hour)) {
                                int index = availableHours.indexOf(hour);
                                availableHours.get(index).setAvailable(false);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        mEtDateTime.setText(String.format("%d/%d/%d", day, month, year));
        this.year = year;
        this.month = month;
        this.day = day;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.YEAR, year);

        loadScheduling(calendar);
    }

    @Override
    public void onClickItem(Hour hour) {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || currentUser.isAnonymous()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, RC_SIGN_IN);
            return;
        }

        Snackbar.make(mRecyclerView, getString(R.string.chosen_time, hour.getFormattedHour()),
                Snackbar.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour.getHour());
        calendar.set(Calendar.MINUTE, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        final String dateWithHour = dateFormat.format(calendar.getTime());

        //Log.e(TAG, "onCancelled", databaseError.toException());

        mDatabase.child("professionalSchedule").orderByChild("idUserProfessionalDay").
                equalTo(geIdWithFormattedDateWithoutHours(calendar, currentUser.getUid(),
                        String.valueOf(mProfessional.getId())))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }

                        Scheduling scheduling = new Scheduling();
                        scheduling.setDate(dateWithHour);
                        scheduling.setIdCompany(mProfessional.getIdCompany());
                        scheduling.setIdProfessional(mProfessional.getId());
                        scheduling.setName(currentUser.getDisplayName());
                        scheduling.setUid(currentUser.getUid());
                        scheduling.setUserEmail(currentUser.getEmail());
                        scheduling.setUserPhone(currentUser.getPhoneNumber());

                        scheduling.setIdCompany_day(
                                geIdWithFormattedDateWithoutHours(calendar, String.valueOf(mProfessional.getIdCompany())));//"10_04-09-2017"
                        scheduling.setIdUserProfessionalDay(
                                geIdWithFormattedDateWithoutHours(calendar, currentUser.getUid(), String.valueOf(mProfessional.getId())));
                        scheduling.setIdProfessionalDay(
                                geIdWithFormattedDateWithoutHours(calendar, String.valueOf(mProfessional.getId())));

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