package com.example.onboardingqueue.model;

import java.util.Map;

public class StatsSummary {
    private Map<String, Long> bookingsPerSlot;
    private Map<String, Long> checkInsPerSlot;
    private long pendingQueueSize;
    private long deniedAttempts;

    public Map<String, Long> getBookingsPerSlot() {
        return bookingsPerSlot;
    }

    public void setBookingsPerSlot(Map<String, Long> bookingsPerSlot) {
        this.bookingsPerSlot = bookingsPerSlot;
    }

    public Map<String, Long> getCheckInsPerSlot() {
        return checkInsPerSlot;
    }

    public void setCheckInsPerSlot(Map<String, Long> checkInsPerSlot) {
        this.checkInsPerSlot = checkInsPerSlot;
    }

    public long getPendingQueueSize() {
        return pendingQueueSize;
    }

    public void setPendingQueueSize(long pendingQueueSize) {
        this.pendingQueueSize = pendingQueueSize;
    }

    public long getDeniedAttempts() {
        return deniedAttempts;
    }

    public void setDeniedAttempts(long deniedAttempts) {
        this.deniedAttempts = deniedAttempts;
    }
}
