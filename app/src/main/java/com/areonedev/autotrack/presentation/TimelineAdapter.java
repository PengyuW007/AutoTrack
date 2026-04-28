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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private List<Task> tasks;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public TimelineAdapter(List<Task> tasks) {
        this.tasks = tasks;
        sortTasksDescending();
    }

    private void sortTasksDescending() {
        if (tasks != null && tasks.size() > 1) {
            tasks.sort((t1, t2) -> {
                if (t1.getDate() == null || t2.getDate() == null) return 0;
                // Descending order: t2 compared to t1
                return t2.getDate().compareTo(t1.getDate());
            });
        }
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        sortTasksDescending();
        notifyDataSetChanged();
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

        // 1. Update Date Format to show Time (HH:mm)
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

        holder.tvTitle.setText(task.getTitle());
        holder.tvDate.setText(dateTimeFormat.format(task.getDate()));

        // 2. Identify High-Intent Tasks (Appointments/Test Drives)
        String titleLower = task.getTitle().toLowerCase();
        boolean isHighIntent = titleLower.contains("appointment") || titleLower.contains("test drive");

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar taskDate = Calendar.getInstance();
        taskDate.setTime(task.getDate());

        // 3. Apply Visual States
        if (task.isCompleted()) {
            // COMPLETED: Gray out
            holder.tvTitle.setTextColor(Color.GRAY);
            holder.tvDate.setTextColor(Color.GRAY);
            holder.ivCheck.setImageResource(R.drawable.ic_check_circle_gray);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        } else if (taskDate.before(today)) {
            // OVERDUE: Red
            holder.tvTitle.setTextColor(Color.RED);
            holder.tvTitle.setText("⚠️ OVERDUE: " + task.getTitle());
            holder.tvDate.setTextColor(Color.RED);
            holder.ivCheck.setImageResource(R.drawable.ic_radio_button_unchecked);
            holder.itemView.setBackgroundColor(Color.parseColor("#FFF0F0"));
        } else {
            // PENDING: Standard or High-Intent Highlight
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);

            if (isHighIntent) {
                // Highlight Appointments/Test Drives in Blue or Purple to show they are special
                holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"));
                holder.tvDate.setTextColor(Color.parseColor("#4CAF50"));
            } else if (task.getTitle().contains("URGENT")) {
                holder.tvDate.setTextColor(Color.RED);
            } else {
                holder.tvDate.setTextColor(Color.parseColor("#4CAF50")); // Green
            }

            holder.ivCheck.setImageResource(R.drawable.ic_radio_button_unchecked);
            holder.ivCheck.setColorFilter(Color.parseColor("#4CAF50"));
        }

        // 4. CLICK LOGIC: Toggle completion and notify activity to recalculate score
        holder.itemView.setOnClickListener(v -> {
            task.setCompleted(!task.isCompleted());
            notifyItemChanged(position);

            // IMPORTANT: You should implement a callback here to LeadDetailsActivity
            // to call accessLeads.updateLead(currentLead) so the score update is saved.
            if (onTaskStatusChangedListener != null) {
                onTaskStatusChangedListener.onTaskStatusChanged(task);
            }
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

    // Add this interface to handle database updates from the Adapter
    public interface OnTaskStatusChangedListener {
        void onTaskStatusChanged(Task task);
    }

    private OnTaskStatusChangedListener onTaskStatusChangedListener;

    public void setOnTaskStatusChangedListener(OnTaskStatusChangedListener listener) {
        this.onTaskStatusChangedListener = listener;
    }
}