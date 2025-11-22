package com.example.onboardingqueue.model;

import java.time.LocalDateTime;

public class DeniedAttempt {
    private final LocalDateTime occurredAt = LocalDateTime.now();
    private final String openId;
    private final String reason;

    public DeniedAttempt(String openId, String reason) {
        this.openId = openId;
        this.reason = reason;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getOpenId() {
        return openId;
    }

    public String getReason() {
        return reason;
    }
}
