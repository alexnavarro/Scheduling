package br.alexandrenavarro.scheduling.holder;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.model.Hour;

/**
 * Created by alexandrenavarro on 14/08/17.
 */

public class SchedulingViewHolder extends RecyclerView.ViewHolder {

    private final TextView mAvailableField;
    private OnItemClickListener onItemClickListener;
    private final TextView mHourField;
    private Hour mHour;

    public SchedulingViewHolder(View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        mHourField = itemView.findViewById(R.id.txt_available_time);
        mAvailableField = itemView.findViewById(R.id.txt_available);
        this.onItemClickListener = onItemClickListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SchedulingViewHolder.this.onItemClickListener.onClickItem(mHour);
            }
        });
    }

    public void bind(Hour hour) {
        if (hour.isAvailable()) {
            this.itemView.setClickable(true);
            mAvailableField.setTextColor(ContextCompat.getColor(mAvailableField.getContext(), android.R.color.holo_green_dark));
            mHourField.setTextColor(ContextCompat.getColor(mHourField.getContext(), android.R.color.holo_green_dark));
            mAvailableField.setText(mAvailableField.getContext().getString(R.string.available).toLowerCase());
        } else {
            this.itemView.setClickable(false);
            mHourField.setTextColor(ContextCompat.getColor(mHourField.getContext(), android.R.color.holo_red_dark));
            mAvailableField.setTextColor(ContextCompat.getColor(mAvailableField.getContext(), android.R.color.holo_red_dark));
            mAvailableField.setText(mAvailableField.getContext().getString(R.string.busy).toLowerCase());
        }

        mHourField.setText(hour.getFormattedHour());
        mHour = hour;
    }
}