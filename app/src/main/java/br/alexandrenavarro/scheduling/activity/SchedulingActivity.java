package br.alexandrenavarro.scheduling.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.alexandrenavarro.scheduling.OnDateChange;
import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.adapter.SchedulingAdapter;
import br.alexandrenavarro.scheduling.fragment.DatePickerFragment;
import br.alexandrenavarro.scheduling.holder.OnItemClickListener;
import br.alexandrenavarro.scheduling.model.Hour;
import br.alexandrenavarro.scheduling.model.Professional;
import br.alexandrenavarro.scheduling.util.DateUtil;
import br.alexandrenavarro.scheduling.util.WorkHoursUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.alexandrenavarro.scheduling.activity.MainActivity.RC_SIGN_IN;

/**
 * Created by alexandrenavarro on 29/08/17.
 */

public class SchedulingActivity extends LifecycleActivity implements OnDateChange, OnItemClickListener, AppCompatCallback {

    public static final String EXTRA_PROFESSIONAL = "Professional";

    @BindView(R.id.txt_description) TextView mTextDescription;
    @BindView(R.id.et_date_time) EditText mEtDateTime;
    @BindView(R.id.recycler_view_available_time) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private LinearLayoutManager mManager;
    private AppCompatDelegate delegate;

    private Professional mProfessional;
    private List<Hour> availableHours = WorkHoursUtils.generate(Calendar.getInstance());
    private SchedulingAdapter mAdapter;
    private int year;
    private int month;
    private int day;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private SchedulingActivityViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        delegate = AppCompatDelegate.create(this, this);
        delegate.installViewFactory();
        super.onCreate(savedInstanceState);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.scheduling_activity);
        ButterKnife.bind(this);
        delegate.setSupportActionBar(mToolbar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProfessional = getIntent().getParcelableExtra(EXTRA_PROFESSIONAL);
        bind();

        model = ViewModelProviders.of(this).get(SchedulingActivityViewModel.class);
        model.loadScheduling(DateUtil.getNextBusinessDay(), mProfessional.getId()).
                observe(this, new Observer<List<Hour>>() {
            @Override
            public void onChanged(@Nullable List<Hour> hours) {
                availableHours.clear();
                availableHours.addAll(hours);
                mAdapter.notifyDataSetChanged();
            }
        });

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

        ActionBar supportActionBar = delegate.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
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
        model.loadingScheduling(calendar, mProfessional.getId());
    }

    @Override
    public void onClickItem(Hour hour) {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || currentUser.isAnonymous()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, RC_SIGN_IN);
            return;
        }

        Snackbar.make(mRecyclerView, getString(R.string.chosen_time, hour.getFormattedHour()),
                Snackbar.LENGTH_SHORT).show();

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour.getHour());
        calendar.set(Calendar.MINUTE, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        model.pickupHour(calendar, mProfessional, currentUser, dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}