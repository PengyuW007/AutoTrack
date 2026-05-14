package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AgendaService {
    private final ScoringService scoringService;
    private final PriorityManager priorityManager;
    private static final double HIGH_PRIORITY_THRESHOLD = ScoringService.THRESHOLD;

    public AgendaService(ScoringService scoringService, PriorityManager priorityManager) {
        this.scoringService = scoringService;
        this.priorityManager = priorityManager;
    }

    public List<Lead> getTodayAgenda(List<Lead> allLeads, List<Task> allTasks, Date targetDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String targetDateStr = sdf.format(targetDate);
        String realTodayStr = sdf.format(new Date());

        boolean isViewingToday = targetDateStr.equals(realTodayStr);
        Set<Lead> agendaSet = new HashSet<>();

        long currentTime = System.currentTimeMillis();
        long threeDaysInMillis = 3L * 24 * 60 * 60 * 1000;

        for (Lead lead : allLeads) {
            // 1. Update Score
            double currentScore = scoringService.calculateScore(lead);
            lead.setLeadScore(currentScore);

            // 2. Check Condition A: Is there a task for THIS specific date?
            boolean hasTaskOnDate = false;

            // Check current follow-up date on the Lead object
            if (lead.getFollowUpDate() != null) {
                if (targetDateStr.equals(sdf.format(lead.getFollowUpDate()))) {
                    hasTaskOnDate = true;
                }
            }

            // NEW: Search the global tasks list for tasks belonging to this lead on this date
            if (!hasTaskOnDate && allTasks != null) {
                for (Task task : allTasks) {
                    // Check if task belongs to this lead AND matches the date
                    if (task.getLead() != null && task.getLead().getLeadID() == lead.getLeadID()) {
                        if (task.getDate() != null && targetDateStr.equals(sdf.format(task.getDate()))) {
                            hasTaskOnDate = true;
                            break;
                        }
                    }
                }
            }

            // 3. Check Condition B: High Priority Suggestion (Only for Today)
            boolean isHighPriority = currentScore >= HIGH_PRIORITY_THRESHOLD;

            long lastInteraction = 0;
            if (lead.getLastInteractionDate() != null) {
                lastInteraction = lead.getLastInteractionDate().getTime();
            } else if (lead.getLeadCreatedAt() != null) {
                lastInteraction = lead.getLeadCreatedAt().getTime();
            }

            boolean isNeglected = (currentTime - lastInteraction) > threeDaysInMillis;

            // FINAL LOGIC:
            // Show if: It has a task (current or historical) on this date
            // OR: It's today, high score, and neglected.
            if (hasTaskOnDate || (isViewingToday && isHighPriority && isNeglected)) {
                agendaSet.add(lead);
            }
        }

        return priorityManager.getPrioritizedList(new ArrayList<>(agendaSet));
    }
}