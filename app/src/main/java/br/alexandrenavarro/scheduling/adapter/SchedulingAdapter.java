package br.alexandrenavarro.scheduling.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.holder.OnItemClickListener;
import br.alexandrenavarro.scheduling.holder.SchedulingViewHolder;
import br.alexandrenavarro.scheduling.model.Hour;

/**
 * Created by alexandrenavarro on 02/09/17.
 */

public class SchedulingAdapter extends RecyclerView.Adapter<SchedulingViewHolder>{

    private List<Hour> hourList;
    private OnItemClickListener onItemClickListener;

    public SchedulingAdapter(List<Hour> hourList, OnItemClickListener onItemClickListener){
        this.hourList = hourList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SchedulingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new SchedulingViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_time_item, parent, false), onItemClickListener);
    }

    @Override
    public void onBindViewHolder(SchedulingViewHolder holder, int position) {
        holder.bind(hourList.get(position));
    }

    @Override
    public int getItemCount() {
        return hourList.size();
    }
}