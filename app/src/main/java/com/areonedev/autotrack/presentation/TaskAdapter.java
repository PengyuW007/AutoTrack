package com.areonedev.autotrack.presentation;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.business.ScoringService;
import com.areonedev.autotrack.objects.Lead;

import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Lead> leads;
    private boolean isPriority;
    private ScoringService scoringService;
    private final int THRESHOLD = 75;

    public TaskAdapter(List<Lead> leads, boolean isPriority,ScoringService scoringService) {
        this.leads = leads;
        this.isPriority = isPriority;
        this.scoringService = scoringService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using a simple list item layout - you can create a custom one if needed
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lead lead = leads.get(position);

        // Calculate the score for this specific lead
        double score = scoringService.calculateScore(lead);

        // Line 1: Name + Score (e.g., "John Doe [Score: 85.5]")
        String nameAndScore = String.format(Locale.getDefault(), "%s %s [Score: %.1f]",
                lead.getLeadFirstName(), lead.getLeadLastName(), score);
        holder.tvName.setText(nameAndScore);

        // Line 2: Task/Stage
        holder.tvStage.setText("Performances: " + lead.getLeadStage());

        // Logic for Completed vs. Priority
        boolean isCompleted = "CLOSED".equalsIgnoreCase(lead.getLeadStage()); // Example condition

        if (isCompleted) {
            holder.tvName.setTextColor(Color.GRAY);
            holder.tvStage.setTextColor(Color.LTGRAY);
            holder.itemView.setAlpha(0.6f); // Make the whole row look faded
        } else {
            holder.itemView.setAlpha(1.0f);
            if (score >= THRESHOLD) {
                holder.tvName.setTextColor(Color.RED); // High priority
                holder.tvName.setTypeface(null, Typeface.BOLD);
            } else {
                holder.tvName.setTextColor(Color.BLACK);
                holder.tvName.setTypeface(null, Typeface.NORMAL);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            // Import android.content.Intent
            Intent intent = new Intent(v.getContext(), LeadDetailsActivity.class);
            // Ensure Lead implements Serializable or Parcelable
            intent.putExtra("SELECTED_LEAD", lead);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (leads != null) ? leads.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvStage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Using standard Android IDs for simple_list_item_2
            tvName = itemView.findViewById(android.R.id.text1);
            tvStage = itemView.findViewById(android.R.id.text2);
        }
    }
}