package com.example.onboardingqueue.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class Booking {
    private final String id = UUID.randomUUID().toString();
    private String openId;
    private LocalDate date;
    private String slotLabel;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status = BookingStatus.RESERVED;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Booking() {
    }

    public Booking(String openId, LocalDate date, SlotTemplate template) {
        this.openId = openId;
        this.date = date;
        this.slotLabel = template.getLabel();
        this.startTime = template.getStartTime();
        this.endTime = template.getEndTime();
    }

    public String getId() {
        return id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSlotLabel() {
        return slotLabel;
    }

    public void setSlotLabel(String slotLabel) {
        this.slotLabel = slotLabel;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
