package com.example.onboardingqueue.repository;

import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.DeniedAttempt;
import com.example.onboardingqueue.model.QueueEntry;
import com.example.onboardingqueue.model.SlotTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryRepository {

    private final List<SlotTemplate> slotTemplates = Collections.synchronizedList(new ArrayList<>());
    private final Map<LocalDate, List<Booking>> bookings = new ConcurrentHashMap<>();
    private final Map<LocalDate, Map<String, List<QueueEntry>>> queues = new ConcurrentHashMap<>();
    private final List<DeniedAttempt> deniedAttempts = Collections.synchronizedList(new ArrayList<>());

    public List<SlotTemplate> getSlotTemplates() {
        return new ArrayList<>(slotTemplates);
    }

    public void replaceSlotTemplates(List<SlotTemplate> templates) {
        slotTemplates.clear();
        slotTemplates.addAll(templates);
    }

    public List<Booking> getBookingsForDate(LocalDate date) {
        return bookings.getOrDefault(date, Collections.emptyList());
    }

    public void saveBooking(Booking booking) {
        bookings.computeIfAbsent(booking.getDate(), d -> Collections.synchronizedList(new ArrayList<>()))
                .add(booking);
    }

    public List<QueueEntry> getQueue(LocalDate date, String slotLabel) {
        return queues.getOrDefault(date, Collections.emptyMap())
                .getOrDefault(slotLabel, Collections.emptyList());
    }

    public QueueEntry enqueue(LocalDate date, String slotLabel, QueueEntry entry) {
        queues.computeIfAbsent(date, d -> new ConcurrentHashMap<>())
                .computeIfAbsent(slotLabel, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(entry);
        return entry;
    }

    public void replaceQueue(LocalDate date, String slotLabel, List<QueueEntry> entries) {
        queues.computeIfAbsent(date, d -> new ConcurrentHashMap<>()).put(slotLabel, entries);
    }

    public void addDeniedAttempt(DeniedAttempt attempt) {
        deniedAttempts.add(attempt);
    }

    public List<DeniedAttempt> getDeniedAttempts() {
        return new ArrayList<>(deniedAttempts);
    }

    public Map<String, Long> aggregateBookings() {
        return bookings.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        booking -> booking.getDate() + " " + booking.getSlotLabel(),
                        Collectors.counting()
                ));
    }

    public List<Booking> findAllBookings() {
        return bookings.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
