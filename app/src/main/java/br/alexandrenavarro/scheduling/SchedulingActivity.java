package br.alexandrenavarro.scheduling;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.alexandrenavarro.scheduling.fragment.DatePickerFragment;
import br.alexandrenavarro.scheduling.model.Professional;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexandrenavarro on 29/08/17.
 */

public class SchedulingActivity extends AppCompatActivity implements OnDateChange{

    public static final String EXTRA_PROFESSIONAL = "Professional";

    @BindView(R.id.txt_description)
    TextView mTextDescription;
    @BindView(R.id.et_date_time)
    EditText mEtDateTime;
    @BindView(R.id.recycler_view_available_time)
    RecyclerView mRecyclerView;

    private Professional mProfessional;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduling_activity);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mProfessional = getIntent().getParcelableExtra(EXTRA_PROFESSIONAL);
        }

        bind();

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

        Calendar calendar = Calendar.getInstance();
        mEtDateTime.setText(new SimpleDateFormat("dd/MM/yyyy").
                format(calendar.getTime()));
    }

    private void loadScheduling() {

    }

    @Override
    public void onDateSet(int year, int month, int day) {
        mEtDateTime.setText(String.format("%d/%d/%d", day, month, year));
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}
