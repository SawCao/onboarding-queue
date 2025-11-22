package com.example.onboardingqueue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Static token for elevating a user to administrator inside the mini program.
     */
    private String adminToken = "letmein";

    /**
     * How many weeks ahead a user can book a slot.
     */
    private int weeksAhead = 4;

    /**
     * Hours before a booking when a reminder notification should be sent.
     */
    private int reminderHoursBefore = 24;

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public int getWeeksAhead() {
        return weeksAhead;
    }

    public void setWeeksAhead(int weeksAhead) {
        this.weeksAhead = weeksAhead;
    }

    public int getReminderHoursBefore() {
        return reminderHoursBefore;
    }

    public void setReminderHoursBefore(int reminderHoursBefore) {
        this.reminderHoursBefore = reminderHoursBefore;
    }
}
