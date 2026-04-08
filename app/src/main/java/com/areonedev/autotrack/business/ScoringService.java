package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ScoringService {
    private final int THRESHOLD = 100;

    public double calculateScore(Lead lead) {

        double score = 0;

        // 1️⃣ Stage Weight
        score += getStageWeight(lead.getLeadStage());

        // 2️⃣ Follow-up urgency
        score += getTimeWeight(lead);

        // 3️⃣ Budget weight
        score += lead.getLeadBudget() / 1000;

        // 4️⃣ Engagement weight (The part you are currently coding)
        // This will subtract points if the lead is "Cold" or "Blocked"
        score += getEngagementWeight(lead);

        return score;
    }

    public List<ScientificTask> getFullTimeline(Lead lead) {
        List<ScientificTask> timeline = new ArrayList<>();
        if (lead == null || lead.getLeadCreatedAt() == null) return timeline;

        int[] milestones = {0, 1, 3, 8, 15, 30, 90, 180, 365};

        Date today = new Date();
        // Normalize today to midnight for comparison
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(today);
        resetTime(todayCal);

        for (int day : milestones) {
            Calendar milestoneCal = Calendar.getInstance();
            milestoneCal.setTime(lead.getLeadCreatedAt());
            milestoneCal.add(Calendar.DAY_OF_YEAR, day);
            resetTime(milestoneCal);

            String mission = getMissionNameByDay(day);
            // Determine if it's completed (if the milestone date is today or in the past)
            boolean isCompleted = !milestoneCal.getTime().after(todayCal.getTime());

            timeline.add(new ScientificTask(mission, milestoneCal.getTime(), isCompleted));
        }
        return timeline;
    }

    // Helper to keep names consistent
    private String getMissionNameByDay(int day) {
        switch (day) {
            case 0: case 1: return "🙏 Gratitude: Thank You & Info Swap";
            case 3: return "💡 New Ideas: Follow up thoughts";
            case 8: return "📈 Market Update: Inventory/Trade-in";
            case 15: return "🎥 Resource: Hidden feature video";
            case 30: return "🔍 Checking In: Specific specs";
            case 90: return "❄️ Seasonal: Service specials";
            case 180: return "🤝 Relationship: High-level check-in";
            case 365: return "🎂 Anniversary: Yearly check-in";
            default: return "Follow up";
        }
    }

    public String getScientificMission(Lead lead, Date targetDate) {
        if (lead == null) return null; // Return null so they don't show on agenda
        if (lead.getLeadCreatedAt() == null) return null;

        // 1. Normalize both dates to Midnight to ensure exact "day" differences
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(lead.getLeadCreatedAt());
        resetTime(startCal);

        Calendar targetCal = Calendar.getInstance();
        targetCal.setTime(targetDate);
        resetTime(targetCal);

        // 2. Calculate the difference in days
        long diffInMillies = targetCal.getTimeInMillis() - startCal.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillies);

        // SAFETY: If the calendar date is BEFORE the lead was created, they shouldn't appear
        if (days < 0) return null;

        double score = calculateScore(lead);
        String stage = (lead.getLeadStage() != null) ? lead.getLeadStage().toUpperCase() : "NEW";

        // 3. Priority/Stage Overrides (These can span multiple days)
        if (score >= THRESHOLD && days >= 2 && days <= 4) {
            return "🚨 URGENT: Customer replied. Follow up within 48h!";
        }

        if ("NEGOTIATION".equals(stage))
            return "🤝 Closing: Address final price/trade-in objections.";

        if ("TEST DRIVE".equals(stage) && days <= 1)
            return "🏎️ Post-Drive: Get feedback on performance.";

        // 4. The Scientific Timeline (Exact Milestones)
        // Jamie and Pengyu were "sticky" because they likely fell into a range logic.
        // By using exact '==' checks, they will only appear on these specific days.
        if (days == 0 || days == 1) return "🙏 Gratitude: Send 'Thank You' & contact info swap.";
        if (days == 3) return "💡 New Ideas: Any new thoughts since your visit?";
        if (days == 8) return "📈 Market Update: Mention similar trade-in/availability.";
        if (days == 15) return "🎥 Resource: Send 'hidden feature' video or finance tip.";
        if (days == 30) return "🔍 Checking In: Offer to watch for specific specs.";
        if (days == 90) return "❄️ Seasonal: New inventory or service specials.";
        if (days == 180) return "🤝 Relationship: High-level check-in (Delayed purchase).";
        if (days == 365) return "🎂 Anniversary: 'Still in that old car?' check-in.";

        // If none of the above match, return null so the lead is hidden from the agenda
        return null;
    }

    private double getStageWeight(String stage) {

        if (stage == null) return 0;

        switch (stage) {
            case "NEW":
                return 60;
            case "CONTACTED":
                return 40;
            case "VISITED":
                return 50;
            case "TEST_DRIVE":
                return 70;
            case "NEGOTIATION":
                return 80;
            case "CLOSED":
                return 0;
            default:
                return 10;
        }
    }


    private double getTimeWeight(Lead lead) {

        if (lead.getLeadFollowUpDate() == null) return 0;

        Calendar todayCal = Calendar.getInstance();
        resetTime(todayCal);

        Calendar followUpCal = Calendar.getInstance();
        followUpCal.setTime(lead.getLeadFollowUpDate());
        resetTime(followUpCal);

        if (followUpCal.before(todayCal)) {
            return 30; // overdue
        }

        if (followUpCal.equals(todayCal)) {
            return 20; // due today
        }

        return 0;
    }

    private double getEngagementWeight(Lead lead) {
        if (lead.getLeadFollowUpDate() == null) return 0;

        // 假设 lead.notes 里可以标记 last response
        // 简单示例：7天未回复 -15，14天未回复 -40，屏蔽 -100
        // 这里用 followUpDate 代替 last response 方便测试
        Calendar today = Calendar.getInstance();
        Calendar lastResponse = Calendar.getInstance();
        lastResponse.setTime(lead.getLeadFollowUpDate());
        resetTime(lastResponse);

        long diff = today.getTimeInMillis() - lastResponse.getTimeInMillis();
        long days = diff / (1000 * 60 * 60 * 24);

        if (days >= 14) {
            return -40;
        } else if (days >= 7) {
            return -15;
        }

        // Check if lead is marked as "Cold" or "Blocked" in notes
        if (lead.getLeadNotes() != null) {
            String notes = lead.getLeadNotes().toLowerCase();
            if (notes.contains("blocked")) return -100;
            if (notes.contains("cold")) return -25;
            if (notes.contains("hot")) return 15; // Bonus for "Hot" leads
        }

        return 0;
    }

    private void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    // Simple inner class or separate file to hold the display data
    public static class ScientificTask {
        public String title;
        public Date date;
        public boolean isCompleted; // True if date is today or in the past

        public ScientificTask(String title, Date date, boolean isCompleted) {
            this.title = title;
            this.date = date;
            this.isCompleted = isCompleted;
        }
    }
}
