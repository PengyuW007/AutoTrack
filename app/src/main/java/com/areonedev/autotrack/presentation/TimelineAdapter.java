package com.areonedev.autotrack.presentation; // Ensure this matches your package structure

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.ScoringService;
import com.areonedev.autotrack.objects.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private List<Task> tasks;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public TimelineAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ensure you have a layout file named item_timeline.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);

        if (task == null || task.getDate() == null) return;

        holder.tvTitle.setText(task.getTitle());
        holder.tvDate.setText(sdf.format(task.getDate()));

        if (task.isCompleted) {
            // Use standard android.graphics.Color
            holder.tvTitle.setTextColor(Color.GRAY);
            holder.tvDate.setTextColor(Color.GRAY);
            holder.ivCheck.setImageResource(R.drawable.ic_check_circle_gray);
        } else {
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.RED);
            holder.ivCheck.setImageResource(R.drawable.ic_radio_button_unchecked);
        }

        // CLICK LOGIC: Toggle completion
        holder.itemView.setOnClickListener(v -> {
            // Toggle the state
            task.setCompleted(!task.isCompleted());

            // Refresh only this item for smooth animation
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView ivCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your item_timeline.xml
            tvTitle = itemView.findViewById(R.id.tvTimelineTitle);
            tvDate = itemView.findViewById(R.id.tvTimelineDate);
            ivCheck = itemView.findViewById(R.id.ivTimelineStatus);
        }
    }
}