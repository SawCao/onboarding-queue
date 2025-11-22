package com.example.onboardingqueue.controller;

import com.example.onboardingqueue.config.AppProperties;
import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.SlotTemplate;
import com.example.onboardingqueue.service.BookingService;
import com.example.onboardingqueue.service.CheckInService;
import com.example.onboardingqueue.service.SchedulingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PublicController {

    private final SchedulingService schedulingService;
    private final BookingService bookingService;
    private final CheckInService checkInService;
    private final AppProperties properties;

    public PublicController(SchedulingService schedulingService, BookingService bookingService, CheckInService checkInService,
                           AppProperties properties) {
        this.schedulingService = schedulingService;
        this.bookingService = bookingService;
        this.checkInService = checkInService;
        this.properties = properties;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        return Map.of(
                "weeksAhead", properties.getWeeksAhead(),
                "templates", schedulingService.getTemplates()
        );
    }

    @GetMapping("/slots")
    public List<Map<String, Object>> listSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SlotTemplate> templates = schedulingService.getTemplates();
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(schedulingService::isDateWithinRange)
                .flatMap(date -> templates.stream().map(template -> Map.of(
                        "date", date,
                        "slotLabel", template.getLabel(),
                        "startTime", template.getStartTime(),
                        "endTime", template.getEndTime(),
                        "capacity", template.getCapacity(),
                        "booked", bookingService.listBookings(date).stream()
                                .filter(b -> b.getSlotLabel().equalsIgnoreCase(template.getLabel()))
                                .count()
                )))
                .collect(Collectors.toList());
    }

    @PostMapping("/bookings")
    public Booking book(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request.openId(), request.date(), request.slotLabel());
    }

    @PostMapping("/check-in")
    public ResponseEntity<CheckInService.CheckInOutcome> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(checkInService.checkIn(request.openId()));
    }

    public record BookingRequest(@NotBlank String openId,
                                 @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 @NotBlank String slotLabel) {
    }

    public record CheckInRequest(@NotBlank String openId) {
    }
}
