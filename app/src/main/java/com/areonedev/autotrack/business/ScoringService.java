package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Task;

import android.os.Build;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Collections;

public class ScoringService {
    private final int THRESHOLD = 100;
    private final int[] SILENT_MILESTONES = {3, 8, 15, 30, 90, 180, 365};

    public double calculateScore(Lead lead) {
        double score = 0;
        // 1️⃣ Stage Weight (Linear)
        score += getStageWeight(lead.getLeadStage());
        // 2️⃣ Follow-up urgency
        score += getTimeWeight(lead);
        // 3️⃣ Engagement weight (Now uses the passed leadTasks)
        score += getEngagementWeight(lead);

        return score;
    }

    public List<Task> getFullTimeline(Lead lead) {
        List<Task> timeline = new ArrayList<>();
        if (lead == null || lead.getLeadCreatedAt() == null) return timeline;

        // Determine Pivot Date: If lead replied, reset timeline to that date.
        Date pivotDate = (lead.getLastInteractionDate() != null && "LEAD".equals(lead.getLastInteractionBy()))
                ? lead.getLastInteractionDate()
                : lead.getLeadCreatedAt();

        Calendar pivotCal = Calendar.getInstance();
        pivotCal.setTime(pivotDate);
        resetTime(pivotCal);

        Calendar todayCal = Calendar.getInstance();
        resetTime(todayCal);

        // 1. Handle Day 1 Gratitude (Always from Creation)
        Calendar day1Cal = Calendar.getInstance();
        day1Cal.setTime(lead.getLeadCreatedAt());
        day1Cal.add(Calendar.DAY_OF_YEAR, 1);
        resetTime(day1Cal);
        if (!day1Cal.after(todayCal)) {
            Task t = new Task(lead, "🙏 Gratitude: Thank You & Info Swap", day1Cal.getTime());
            t.setCompleted(true);
            timeline.add(t);
        }

        // 2. Handle 48h Urgency if Lead Replied
        long daysFromPivot = getDaysDiff(pivotCal, todayCal);
        if ("LEAD".equals(lead.getLastInteractionBy())) {
            Task urgent = new Task(lead, "🚨 URGENT: Lead replied. Respond within 48h!", todayCal.getTime());
            urgent.setCompleted(daysFromPivot > 2);
            timeline.add(urgent);
        }

        // 3. Silent Milestones from Pivot
        for (int milestone : SILENT_MILESTONES) {
            Calendar mCal = (Calendar) pivotCal.clone();
            mCal.add(Calendar.DAY_OF_YEAR, milestone);
            if (!mCal.after(todayCal)) {
                Task t = new Task(lead, getMissionNameByDay(milestone), mCal.getTime());
                t.setCompleted(mCal.before(todayCal));
                timeline.add(t);
            }
        }

        // --- 2. ADD MANUAL USER TASKS
        // This ensures the "Appointment", "Test Drive", etc., appear in the window
        if (lead.getLeadTasks() != null && !lead.getLeadTasks().isEmpty()) {
            timeline.addAll(lead.getLeadTasks());
        }

        // --- 3. SORT BY DATE
        // This ensures the timeline flows correctly from past to future
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            timeline.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        } else {
            Collections.sort(timeline, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        }

        return timeline;
    }

    public String getScientificMission(Lead lead, Date targetDate) {
        if (lead == null || lead.getLeadCreatedAt() == null) return null;

        Calendar targetCal = Calendar.getInstance();
        targetCal.setTime(targetDate);
        resetTime(targetCal);

        // Fixed Day 1 Logic
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(lead.getLeadCreatedAt());
        resetTime(createdCal);
        if (getDaysDiff(createdCal, targetCal) == 1) {
            return "🙏 Gratitude: Thank You & Info Swap";
        }

        // Dynamic Pivot Logic
        Date pivotDate = (lead.getLastInteractionDate() != null && "LEAD".equals(lead.getLastInteractionBy()))
                ? lead.getLastInteractionDate()
                : lead.getLeadCreatedAt();
        Calendar pivotCal = Calendar.getInstance();
        pivotCal.setTime(pivotDate);
        resetTime(pivotCal);

        long daysFromPivot = getDaysDiff(pivotCal, targetCal);

        // 48h Response Rule
        if ("LEAD".equals(lead.getLastInteractionBy()) && daysFromPivot <= 2) {
            return "🚨 URGENT: Lead replied. Respond within 48h!";
        }

        // Silent Timeline
        for (int milestone : SILENT_MILESTONES) {
            if (daysFromPivot == milestone) {
                return getMissionNameByDay(milestone);
            }
        }

        // Score-based Overrides (e.g. Negotiation)
        double score = calculateScore(lead);
        if (score >= THRESHOLD) {
            return "🔥 High Priority: Nurture " + lead.getLeadStage() + " (Score: " + (int) score + ")";
        }

        return "Standard Follow-up: " + lead.getLeadStage();
    }

    private String getMissionNameByDay(int day) {
        switch (day) {
            case 3:
                return "💡 New Ideas: Follow up thoughts";
            case 8:
                return "📈 Market Update: Inventory/Trade-in";
            case 15:
                return "🎥 Resource: Hidden feature video";
            case 30:
                return "🔍 Checking In: Specific specs";
            case 90:
                return "❄️ Seasonal: Service specials";
            case 180:
                return "🤝 Relationship: High-level check-in";
            case 365:
                return "🎂 Anniversary: Yearly check-in";
            default:
                return "Follow up";
        }
    }

    private double getStageWeight(String stage) {
        if (stage == null) return 0;
        switch (stage.toUpperCase()) {
            case "NEW":
                return 40;
            case "CONTACTED":
                return 50;
            case "VISITED":
                return 60;
            case "TEST_DRIVE":
                return 70;
            case "NEGOTIATION":
                return 100;
            case "CLOSED":
                return 0;
            default:
                return 10;
        }
    }

    private double getTimeWeight(Lead lead) {
        // Use Pivot Date to calculate how "stale" the lead is
        Date pivotDate = (lead.getLastInteractionDate() != null) ? lead.getLastInteractionDate() : lead.getLeadCreatedAt();
        Calendar pivotCal = Calendar.getInstance();
        pivotCal.setTime(pivotDate);
        resetTime(pivotCal);

        Calendar today = Calendar.getInstance();
        resetTime(today);

        long daysSilent = getDaysDiff(pivotCal, today);
        if (daysSilent > 7) return 30; // Overdue/Stale
        if (daysSilent > 3) return 15; // Getting cold
        return 0;
    }

    private double getEngagementWeight(Lead lead) {
        double engagementScore = 0;
        List<Task> leadTasks = lead.getLeadTasks();

        if (leadTasks != null) {
            int validVisitCount = 0;
            for (Task task : leadTasks) {
                // Only count tasks that are COMPLETED and match high-intent keywords
                if (task.isCompleted()) {
                    String desc = task.getTitle().toLowerCase();
                    if (desc.contains("appointment") || desc.contains("test drive") || desc.contains("visited")) {
                        validVisitCount++;
                    }
                }
            }
            // Darryl gets +25 for every completed visit/appointment
            engagementScore += (validVisitCount * 10);
        }

        return engagementScore;
    }

    private long getDaysDiff(Calendar start, Calendar end) {
        return TimeUnit.MILLISECONDS.toDays(end.getTimeInMillis() - start.getTimeInMillis());
    }

    private void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

}
