package com.example.onboardingqueue.service;

import com.example.onboardingqueue.config.AppProperties;
import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.repository.InMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final InMemoryRepository repository;
    private final AppProperties properties;
    private final Set<String> remindedBookingIds = new HashSet<>();

    public NotificationService(InMemoryRepository repository, AppProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void sendReminders() {
        LocalDate today = LocalDate.now();
        LocalDateTime thresholdStart = LocalDateTime.now().plusHours(properties.getReminderHoursBefore());
        LocalDateTime thresholdEnd = thresholdStart.plusMinutes(30);

        for (int i = 0; i <= properties.getWeeksAhead() * 7; i++) {
            LocalDate targetDate = today.plusDays(i);
            List<Booking> bookings = repository.getBookingsForDate(targetDate);
            for (Booking booking : bookings) {
                LocalDateTime bookingStart = LocalDateTime.of(booking.getDate(), booking.getStartTime());
                if (bookingStart.isAfter(thresholdStart) && bookingStart.isBefore(thresholdEnd)
                        && !remindedBookingIds.contains(booking.getId())) {
                    log.info("Reminder: send to {} for {} {}", booking.getOpenId(), booking.getDate(), booking.getSlotLabel());
                    remindedBookingIds.add(booking.getId());
                }
            }
        }
    }

    public void sendQueueReminder(String openId, LocalDate date, LocalTime startTime, int number) {
        log.info("Queue reminder -> {} for {} {} #{}", openId, date, startTime, number);
    }
}
