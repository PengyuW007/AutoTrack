package com.areonedev.autotrack.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.areonedev.autotrack.R;
import com.areonedev.autotrack.objects.Lead;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LeadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // This list holds both Strings (Headers) and Leads (Cards)
    private List<Object> displayItems = new ArrayList<>();

    public LeadAdapter(List<Lead> leads) {
        processLeadsDates(leads);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_lead, parent, false);
            return new LeadViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayItems.get(position);

        if (holder instanceof HeaderViewHolder && item instanceof String) {
            ((HeaderViewHolder) holder).dateText.setText((String) item);
        } else if (holder instanceof LeadViewHolder && item instanceof Lead) {
            Lead lead = (Lead) item;
            LeadViewHolder lvh = (LeadViewHolder) holder;
            lvh.name.setText(lead.getLeadName());
            lvh.phone.setText(lead.getLeadPhoneNumber());
            lvh.stage.setText(lead.getLeadStage());
            lvh.vehicle.setText(lead.getLeadVehicleInterest());
        }
    }

    @Override
    public int getItemCount() {
        // FIX: Must return the size of displayItems (Headers + Leads)
        return displayItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (displayItems.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void updateList(List<Lead> newList) {
        // FIX: Must re-process dates when the list is updated (e.g., during search)
        processLeadsDates(newList);
        notifyDataSetChanged();
    }

    private void processLeadsDates(List<Lead> leads) {
        displayItems.clear();
        if (leads == null || leads.isEmpty()) return;

        String lastDateLabel = "";
        for (Lead lead : leads) {
            // 1. Get the raw string (e.g., "2026-03-22 14:30:00")
            String rawCreatedAt = (lead.getLeadCreatedAt() != null) ? lead.getLeadCreatedAt().toString() : "";

            // 2. Truncate to just the date part "2026-03-22" (first 10 characters)
            String dateOnly = (rawCreatedAt.length() >= 10) ? rawCreatedAt.substring(0, 10) : rawCreatedAt;

            String currentLabel = getRelativeDate(dateOnly);

            if (!currentLabel.equals(lastDateLabel)) {
                displayItems.add(currentLabel);
                lastDateLabel = currentLabel;
            }
            displayItems.add(lead);
        }
    }

    private String getRelativeDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "Unknown Date";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);

            Calendar cal = Calendar.getInstance();
            String today = sdf.format(cal.getTime());

            cal.add(Calendar.DATE, -1);
            String yesterday = sdf.format(cal.getTime());

            if (dateStr.equals(today)) return "Today";
            if (dateStr.equals(yesterday)) return "Yesterday";

            // 3. Updated format: "dd mmm yyyy"
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    static class LeadViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, stage, vehicle;
        public LeadViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_lead_name);
            phone = itemView.findViewById(R.id.text_lead_phone);
            stage = itemView.findViewById(R.id.text_lead_stage);
            vehicle = itemView.findViewById(R.id.text_lead_vehicle);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.text_date_header);
        }
    }
}