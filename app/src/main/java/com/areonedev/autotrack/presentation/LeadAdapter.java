package com.areonedev.autotrack.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_lead, parent, false);
            return new LeadViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 1. Check if the item at this position is a Lead
        Object item = displayItems.get(position);

        if (holder instanceof LeadViewHolder && item instanceof Lead) {
            LeadViewHolder lvh = (LeadViewHolder) holder;
            Lead lead = (Lead) item; // This fixes the "Incompatible types" error

            lvh.name.setText(lead.getLeadFirstName() + " " + lead.getLeadLastName());
            lvh.phone.setText(lead.getLeadPhoneNumber());
            lvh.stage.setText(lead.getLeadStage());

            // --- VEHICLE DISPLAY LOGIC ---
            Vehicle vi = lead.getLeadVehicleInterest();
            if (vi != null) {
                String row1 = (vi.getYear() != null ? vi.getYear() : "") + " " + (vi.getMake() != null ? vi.getMake() : "");
                String row2 = (vi.getModel() != null ? vi.getModel() : "Unknown Model");
                String row3 = (vi.getTrim() != null ? vi.getTrim() : "");

                android.text.SpannableStringBuilder builder = new android.text.SpannableStringBuilder();
                // 1. Add Year and Make
                if (vi.getYear() != null) builder.append(vi.getYear()).append(" ");
                if (vi.getMake() != null) builder.append(vi.getMake()).append(" ");

                // 2. Add Model (Highlighted/Bold)
                int start = builder.length();
                String model = (vi.getModel() != null ? vi.getModel() : "Unknown Model");
                builder.append(model);
                builder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        start, builder.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 3. Add Trim
                if (vi.getTrim() != null && !vi.getTrim().isEmpty()) {
                    builder.append(" ").append(vi.getTrim());
                }

                lvh.vehicle.setText(builder);

//                builder.append(row1).append("\n");
//
//                int start = builder.length();
//                builder.append(row2);
//                builder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
//                        start, builder.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new android.text.style.RelativeSizeSpan(1.1f),
//                        start, builder.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.append("\n");
//
//                builder.append(row3);
//                lvh.vehicle.setText(builder);
            } else {
                lvh.vehicle.setText("No Vehicle Interest");
            }
        }
        else if (holder instanceof HeaderViewHolder && item instanceof String) {
            // 2. Handle the Header display
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            hvh.dateText.setText((String) item);
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

        // Use a formatter to get a consistent "yyyy-MM-dd" string for comparison
        SimpleDateFormat compareFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String lastDateLabel = "";

        for (Lead lead : leads) {
            Date createdAt = lead.getLeadCreatedAt();
            if (createdAt == null) continue;

            // 1. Convert the Lead's date to a comparable string
            String dateKey = compareFormat.format(createdAt);

            // 2. Get the relative label (Today, Yesterday, or dd MMM yyyy)
            String currentLabel = getRelativeDate(dateKey);

            // 3. Add header if it's a new day
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
            Date leadDate = sdf.parse(dateStr);
            if (leadDate == null) return dateStr;

            // 1. Get Calendar instances for both dates
            Calendar today = Calendar.getInstance();
            Calendar leadCal = Calendar.getInstance();
            leadCal.setTime(leadDate);

            // 2. Check for "Today" (Same Year and Same Day of Year)
            if (today.get(Calendar.YEAR) == leadCal.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == leadCal.get(Calendar.DAY_OF_YEAR)) {
                return "Today";
            }

            // 3. Check for "Yesterday"
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            if (yesterday.get(Calendar.YEAR) == leadCal.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == leadCal.get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday";
            }

            // 4. Fallback for older dates: "23 Mar 2026"
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(leadDate);

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