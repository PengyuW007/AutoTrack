package com.areonedev.autotrack.business;

import com.areonedev.autotrack.objects.Lead;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
public class PriorityManager {
    private PriorityQueue<Lead> priorityQueue;
    private ScoringService scoringService;

    // =========================
    // Constructor
    // =========================
    public PriorityManager(ScoringService scoringService) {
        this.scoringService = scoringService;

        // Comparator: score 越高优先级越高
        this.priorityQueue = new PriorityQueue<>(new Comparator<Lead>() {
            @Override
            public int compare(Lead l1, Lead l2) {
                // 逆序，大分数排前面
                return Double.compare(l2.getLeadScore(), l1.getLeadScore());
            }
        });
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
        return new ArrayList<>(priorityQueue);
    }
}
