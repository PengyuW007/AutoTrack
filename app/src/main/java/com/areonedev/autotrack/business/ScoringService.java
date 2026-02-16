package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;

import java.util.Calendar;
public class ScoringService {
    public double calculateScore(Lead lead) {

        double score = 0;

        // 1️⃣ Stage Weight
        score += getStageWeight(lead.getStage());

        // 2️⃣ Follow-up urgency
        score += getTimeWeight(lead);

        // 3️⃣ Budget weight
        score += lead.getBudget() / 1000;

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

        if (lead.getFollowUpDate() == null) return 0;

        Calendar todayCal = Calendar.getInstance();
        resetTime(todayCal);

        Calendar followUpCal = Calendar.getInstance();
        followUpCal.setTime(lead.getFollowUpDate());
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
        if (lead.getFollowUpDate() == null) return 0;

        // 假设 lead.notes 里可以标记 last response
        // 简单示例：7天未回复 -15，14天未回复 -40，屏蔽 -100
        // 这里用 followUpDate 代替 last response 方便测试
        Calendar today = Calendar.getInstance();
        Calendar lastResponse = Calendar.getInstance();
        lastResponse.setTime(lead.getFollowUpDate());
        resetTime(lastResponse);

        long diff = today.getTimeInMillis() - lastResponse.getTimeInMillis();
        long days = diff / (1000 * 60 * 60 * 24);

        if (days >= 14) {
            return -40;
        } else if (days >= 7) {
            return -15;
        }

        // 屏蔽/不理可通过 lead.notes 或新增字段标记
        // if (lead.isBlocked()) return -100;

        return 0;
    }

    private void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
