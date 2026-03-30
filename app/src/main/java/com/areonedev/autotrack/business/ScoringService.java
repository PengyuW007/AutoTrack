package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;

import java.util.Calendar;
public class ScoringService {
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
}
