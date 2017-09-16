package br.alexandrenavarro.scheduling.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.activity.SchedulingActivity;
import br.alexandrenavarro.scheduling.model.Professional;

/**
 * Created by alexandrenavarro on 14/08/17.
 */

public class ProfessionalHolder extends RecyclerView.ViewHolder{

    private final TextView mNameField;
    private final TextView mSchedulingField;
    private final TextView mDayOfWeekField;
    private Professional mProfessional;

    public ProfessionalHolder(View itemView) {
        super(itemView);
        mNameField = itemView.findViewById(R.id.txt_name);
        mSchedulingField = itemView.findViewById(R.id.txt_scheduling);
        mDayOfWeekField = itemView.findViewById(R.id.txt_today);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SchedulingActivity.class);
            intent.putExtra(SchedulingActivity.EXTRA_PROFESSIONAL, mProfessional);
            view.getContext().startActivity(intent);
        });
    }

    public void bind(Professional professional, Set<Integer> hours, String weekDay) {
        mNameField.setText(professional.getName());

        if(hours != null)
            mSchedulingField.setText(formatHour(hours));

        mDayOfWeekField.setText(weekDay);

        mProfessional = professional;
    }

    private String formatHour(Set<Integer> hours){
        StringBuilder builder = new StringBuilder();
        for(Integer hour : hours){
            if(builder.length() > 0){
                builder.append(" ").append("|").append(" ");
            }

            builder.append(hour).append(":").append("00");
        }

        return builder.toString();
    }
}