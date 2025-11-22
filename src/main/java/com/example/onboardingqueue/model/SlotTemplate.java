package com.example.onboardingqueue.model;

import java.time.LocalTime;

public class SlotTemplate {
    private String label;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;

    public SlotTemplate() {
    }

    public SlotTemplate(String label, LocalTime startTime, LocalTime endTime, int capacity) {
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
