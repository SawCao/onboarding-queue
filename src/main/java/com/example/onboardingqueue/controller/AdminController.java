package com.example.onboardingqueue.controller;

import com.example.onboardingqueue.config.AppProperties;
import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.QueueEntry;
import com.example.onboardingqueue.model.SlotTemplate;
import com.example.onboardingqueue.model.StatsSummary;
import com.example.onboardingqueue.service.BookingService;
import com.example.onboardingqueue.service.NotificationService;
import com.example.onboardingqueue.service.QueueService;
import com.example.onboardingqueue.service.SchedulingService;
import com.example.onboardingqueue.service.StatsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SchedulingService schedulingService;
    private final BookingService bookingService;
    private final QueueService queueService;
    private final NotificationService notificationService;
    private final StatsService statsService;
    private final AppProperties properties;

    public AdminController(SchedulingService schedulingService, BookingService bookingService, QueueService queueService,
                           NotificationService notificationService, StatsService statsService, AppProperties properties) {
        this.schedulingService = schedulingService;
        this.bookingService = bookingService;
        this.queueService = queueService;
        this.notificationService = notificationService;
        this.statsService = statsService;
        this.properties = properties;
    }

    @PutMapping("/config/templates")
    public List<SlotTemplate> updateTemplates(@RequestBody List<SlotTemplate> templates) {
        schedulingService.updateTemplates(templates);
        return schedulingService.getTemplates();
    }

    @PutMapping("/config/weeks-ahead")
    public Map<String, Object> updateWeeksAhead(@RequestParam int weeksAhead) {
        properties.setWeeksAhead(weeksAhead);
        return Map.of("weeksAhead", properties.getWeeksAhead());
    }

    @GetMapping("/bookings")
    public List<Booking> bookings(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.listBookings(date);
    }

    @GetMapping("/queue")
    public List<QueueEntry> queue(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  @RequestParam String slotLabel) {
        return queueService.getQueue(date, slotLabel);
    }

    @PostMapping("/queue/call-next")
    public Optional<QueueEntry> callNext(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                         @RequestParam String slotLabel) {
        Optional<QueueEntry> entry = queueService.callNext(date, slotLabel);
        entry.ifPresent(e -> notificationService.sendQueueReminder(e.getOpenId(), date, e.getStartTime(), e.getNumber()));
        return entry;
    }

    @PostMapping("/queue/skip")
    public Optional<QueueEntry> skip(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                     @RequestParam String slotLabel,
                                     @RequestParam int number) {
        return queueService.skip(date, slotLabel, number);
    }

    @PostMapping("/queue/serve")
    public Optional<QueueEntry> serve(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam String slotLabel,
                                      @RequestParam int number) {
        return queueService.serve(date, slotLabel, number);
    }

    @PostMapping("/remind")
    public Map<String, String> manualReminder(@Valid @RequestBody ReminderRequest request) {
        notificationService.sendQueueReminder(request.openId(), request.date(), request.startTime(), request.number());
        return Map.of("status", "ok");
    }

    @GetMapping("/stats")
    public StatsSummary stats() {
        return statsService.summarize();
    }

    public record ReminderRequest(@NotBlank String openId,
                                  @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  @NotNull java.time.LocalTime startTime,
                                  @NotNull Integer number) {}
}
