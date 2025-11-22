package com.example.onboardingqueue.service;

import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.DeniedAttempt;
import com.example.onboardingqueue.model.QueueEntry;
import com.example.onboardingqueue.model.SlotTemplate;
import com.example.onboardingqueue.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class CheckInService {

    private final BookingService bookingService;
    private final SchedulingService schedulingService;
    private final QueueService queueService;
    private final InMemoryRepository repository;

    public CheckInService(BookingService bookingService, SchedulingService schedulingService, QueueService queueService, InMemoryRepository repository) {
        this.bookingService = bookingService;
        this.schedulingService = schedulingService;
        this.queueService = queueService;
        this.repository = repository;
    }

    public CheckInOutcome checkIn(String openId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        Optional<SlotTemplate> templateOpt = schedulingService.findTemplateForTime(now);
        if (templateOpt.isEmpty()) {
            repository.addDeniedAttempt(new DeniedAttempt(openId, "No active slot right now"));
            return CheckInOutcome.denied("当前时间不在可办理时段，请联系前台");
        }

        SlotTemplate template = templateOpt.get();
        Optional<Booking> existing = bookingService.findBookingForUser(openId, today, template.getLabel());
        Booking booking = existing.orElse(null);

        if (booking == null) {
            try {
                booking = bookingService.createBooking(openId, today, template.getLabel());
            } catch (IllegalStateException e) {
                repository.addDeniedAttempt(new DeniedAttempt(openId, "Slot full during check-in"));
                return CheckInOutcome.denied("当前时段人数已满，请稍后再试或联系前台");
            }
        }

        bookingService.markCheckedIn(booking);
        QueueEntry entry = queueService.addToQueue(booking);
        return CheckInOutcome.success(booking.getSlotLabel(), entry.getNumber());
    }

    public record CheckInOutcome(boolean success, String message, String slotLabel, Integer queueNumber) {
        public static CheckInOutcome denied(String message) {
            return new CheckInOutcome(false, message, null, null);
        }

        public static CheckInOutcome success(String slotLabel, int queueNumber) {
            return new CheckInOutcome(true, "签到成功", slotLabel, queueNumber);
        }
    }
}
