package com.areonedev.autotrack.presentation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {
    private final OnDateClickListener listener;
    private final List<Date> days;
   // private final Calendar currentCal; // Added this field
    private final Date selectedDate;

    public interface OnDateClickListener {
        void onDateClick(Date date);
    }

    // Update constructor to accept two parameters
    public WeekAdapter(List<Date> days, Date selectedDate, OnDateClickListener listener) {
        this.days = days;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = days.get(position);
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dayNumFormat = new SimpleDateFormat("d", Locale.getDefault());

        holder.tvDayName.setText(dayNameFormat.format(date));
        holder.tvDayNumber.setText(dayNumFormat.format(date));

        // Highlight logic: Check if this date is "Today"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        if (selectedDate != null && sdf.format(date).equals(sdf.format(selectedDate))) {
            // Highlight logic
            holder.tvDayNumber.setBackgroundResource(R.drawable.circle_background_light_blue);
            holder.tvDayNumber.setTextColor(Color.WHITE);
        } else {
            holder.tvDayNumber.setBackground(null);
            holder.tvDayNumber.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> listener.onDateClick(date));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber;
        ViewHolder(View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }
    }
}