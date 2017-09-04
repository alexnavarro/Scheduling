package br.alexandrenavarro.scheduling.fragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import br.alexandrenavarro.scheduling.OnDateChange;
import br.alexandrenavarro.scheduling.util.DateUtil;

/**
 * Created by alexandrenavarro on 29/08/17.
 */

public class DatePickerFragment extends DialogFragment implements OnDateSetListener {

    private OnDateChange mCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = DateUtil.getNextBusinessDay();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mCallback.onDateSet(year, month, day);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnDateChange) {
            mCallback = (OnDateChange) context;
        } else {
            throw new RuntimeException("OnDateChange not implemented in context");
        }

    }
}