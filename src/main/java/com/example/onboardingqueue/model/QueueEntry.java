package com.example.onboardingqueue.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class QueueEntry {
    private int number;
    private QueueStatus status = QueueStatus.WAITING;
    private String bookingId;
    private String openId;
    private LocalDate date;
    private String slotLabel;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public QueueEntry() {
    }

    public QueueEntry(int number, Booking booking) {
        this.number = number;
        this.bookingId = booking.getId();
        this.openId = booking.getOpenId();
        this.date = booking.getDate();
        this.slotLabel = booking.getSlotLabel();
        this.startTime = booking.getStartTime();
        this.endTime = booking.getEndTime();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
