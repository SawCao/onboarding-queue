package com.example.onboardingqueue.service;

import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.BookingStatus;
import com.example.onboardingqueue.model.SlotTemplate;
import com.example.onboardingqueue.repository.InMemoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final InMemoryRepository repository;
    private final SchedulingService schedulingService;

    public BookingService(InMemoryRepository repository, SchedulingService schedulingService) {
        this.repository = repository;
        this.schedulingService = schedulingService;
    }

    public Booking createBooking(String openId, LocalDate date, String slotLabel) {
        validate(openId, date, slotLabel);
        SlotTemplate template = schedulingService.findTemplateByLabel(slotLabel)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        List<Booking> bookingsForDate = repository.getBookingsForDate(date);
        long currentCount = bookingsForDate.stream()
                .filter(b -> b.getSlotLabel().equalsIgnoreCase(slotLabel))
                .count();
        if (currentCount >= template.getCapacity()) {
            throw new IllegalStateException("Slot is full");
        }

        Booking booking = new Booking(openId, date, template);
        repository.saveBooking(booking);
        return booking;
    }

    public Optional<Booking> findBookingForUser(String openId, LocalDate date, String slotLabel) {
        return repository.getBookingsForDate(date).stream()
                .filter(b -> b.getOpenId().equals(openId) && b.getSlotLabel().equalsIgnoreCase(slotLabel))
                .findFirst();
    }

    public List<Booking> listBookings(LocalDate date) {
        return repository.getBookingsForDate(date);
    }

    public Booking markCheckedIn(Booking booking) {
        booking.setStatus(BookingStatus.CHECKED_IN);
        return booking;
    }

    private void validate(String openId, LocalDate date, String slotLabel) {
        if (!StringUtils.hasText(openId) || date == null || !StringUtils.hasText(slotLabel)) {
            throw new IllegalArgumentException("Missing booking information");
        }
        if (!schedulingService.isDateWithinRange(date)) {
            throw new IllegalArgumentException("Date is outside the allowed booking window");
        }
    }
}
