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
//import com.google.type.Date;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Lead> leads;
    private ScoringService scoringService;
    private final int THRESHOLD = 100;
    private View emptyView;

    public TaskAdapter(List<Lead> leads,ScoringService scoringService,View emptyView) {
        this.leads = leads;
        this.scoringService = scoringService;
        this.emptyView = emptyView;
        toggleEmptyState(); // Check visibility immediately on creation
    }

    private void toggleEmptyState() {
        if (emptyView != null) {
            if (getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    public void updateData(List<Lead> newLeads) {
        this.leads = newLeads;
        notifyDataSetChanged();
        toggleEmptyState();
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

        // Calculate the Score and Mission for this specific lead
        double score = scoringService.calculateScore(lead);
        String missionTitle = getMissionTitle(lead);
        String missionDesc = scoringService.getScientificMission(lead);

        // Line 1: Name + Mission Title (e.g., "John Doe | 🏎️ Test Drive")
        holder.tvName.setText(String.format("%s %s | %s", lead.getLeadFirstName(), lead.getLeadLastName(), missionTitle));

        // Line 2: Mission Description + Stage (Scientific Context)
        String performanceText = String.format("Mission: %s [%s]", missionDesc, lead.getLeadStage());
        holder.tvStage.setText(performanceText);

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

    // Helper for Title
    private String getMissionTitle(Lead lead) {
        String stage = (lead.getLeadStage() != null) ? lead.getLeadStage().toUpperCase() : "NEW";
        switch (stage) {
            case "NEW": return "👋 Initial Contact";
            case "DISCOVERY": return "🔍 Discovery";
            case "TEST DRIVE": return "🏎️ Test Drive";
            case "NEGOTIATION": return "🤝 Negotiation";
            default: return "📞 Follow-up";
        }
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