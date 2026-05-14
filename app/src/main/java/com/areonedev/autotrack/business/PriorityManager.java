package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
public class PriorityManager {
    private PriorityQueue<Lead> priorityQueue;
    private ScoringService scoringService;
    private static double THRESHOLD = ScoringService.THRESHOLD;
    // =========================
    // Constructor
    // =========================
    public PriorityManager(ScoringService scoringService) {
        this.scoringService = scoringService;
        // PriorityQueue and compare handles the "Queue" structure: Highest score stays at the head
        this.priorityQueue = new PriorityQueue<>((l1, l2) -> Double.compare(l2.getLeadScore(), l1.getLeadScore()));
    }

    /**
     * New Method for Calendar:
     * Takes a list of leads (e.g., from a specific date),
     * scores them, and returns them in priority order.
     */
    public List<Lead> getPrioritizedList(List<Lead> inputLeads) {
        priorityQueue.clear();
        if (inputLeads != null) {
            for (Lead lead : inputLeads) {
                // Ensure score is set before adding to queue
                if (lead.getLeadScore() == 0) {
                    lead.setLeadScore(scoringService.calculateScore(lead));
                }
                priorityQueue.add(lead);
            }
        }

        List<Lead> sortedList = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            sortedList.add(priorityQueue.poll());
        }
        return sortedList;
    }

    // =========================
    // Add or Update Lead
    // =========================
    public void addOrUpdateLead(Lead lead) {
        // 如果已经存在，先删除
        priorityQueue.remove(lead);

        // 重新计算 score
        double score = scoringService.calculateScore(lead);
        lead.setLeadScore(score);

        // 添加到队列
        priorityQueue.add(lead);
    }

    // =========================
    // Remove Lead
    // =========================
    public void removeLead(Lead lead) {
        priorityQueue.remove(lead);
    }

    // =========================
    // Get Top Lead
    // =========================
    public Lead peekTopLead() {
        return priorityQueue.peek();
    }

    // =========================
    // Get All Leads Sorted
    // =========================
    public List<Lead> getAllLeadsSorted() {
        List<Lead> list = new ArrayList<>(priorityQueue);
        list.sort((l1, l2) -> Double.compare(l2.getLeadScore(), l1.getLeadScore()));
        return list;
    }
}
