package com.example.onboardingqueue.service;

import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.BookingStatus;
import com.example.onboardingqueue.model.DeniedAttempt;
import com.example.onboardingqueue.model.QueueEntry;
import com.example.onboardingqueue.model.QueueStatus;
import com.example.onboardingqueue.model.StatsSummary;
import com.example.onboardingqueue.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final InMemoryRepository repository;

    public StatsService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public StatsSummary summarize() {
        StatsSummary summary = new StatsSummary();
        Map<String, Long> bookings = repository.aggregateBookings();

        List<Booking> allBookings = repository.findAllBookings();

        Map<String, Long> checkIns = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_IN)
                .collect(Collectors.groupingBy(
                        b -> b.getDate() + " " + b.getSlotLabel(),
                        Collectors.counting()
                ));

        List<QueueEntry> queueEntries = allBookings.stream()
                .flatMap(b -> repository.getQueue(b.getDate(), b.getSlotLabel()).stream())
                .toList();

        long pending = queueEntries.stream()
                .filter(q -> q.getStatus() == QueueStatus.WAITING || q.getStatus() == QueueStatus.CALLED)
                .count();

        List<DeniedAttempt> denied = repository.getDeniedAttempts();

        summary.setBookingsPerSlot(bookings);
        summary.setCheckInsPerSlot(checkIns);
        summary.setPendingQueueSize(pending);
        summary.setDeniedAttempts(denied.size());
        return summary;
    }
}
