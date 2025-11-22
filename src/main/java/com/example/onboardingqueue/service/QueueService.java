package com.example.onboardingqueue.service;

import com.example.onboardingqueue.model.Booking;
import com.example.onboardingqueue.model.QueueEntry;
import com.example.onboardingqueue.model.QueueStatus;
import com.example.onboardingqueue.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final InMemoryRepository repository;

    public QueueService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public QueueEntry addToQueue(Booking booking) {
        List<QueueEntry> entries = new ArrayList<>(repository.getQueue(booking.getDate(), booking.getSlotLabel()));
        int nextNumber = entries.size() + 1;
        QueueEntry entry = new QueueEntry(nextNumber, booking);
        repository.enqueue(booking.getDate(), booking.getSlotLabel(), entry);
        return entry;
    }

    public List<QueueEntry> getQueue(LocalDate date, String slotLabel) {
        return repository.getQueue(date, slotLabel);
    }

    public Optional<QueueEntry> callNext(LocalDate date, String slotLabel) {
        List<QueueEntry> queue = new ArrayList<>(repository.getQueue(date, slotLabel));
        Optional<QueueEntry> next = queue.stream()
                .filter(entry -> entry.getStatus() == QueueStatus.WAITING)
                .min(Comparator.comparingInt(QueueEntry::getNumber));
        next.ifPresent(entry -> entry.setStatus(QueueStatus.CALLED));
        repository.replaceQueue(date, slotLabel, queue);
        return next;
    }

    public Optional<QueueEntry> skip(LocalDate date, String slotLabel, int number) {
        List<QueueEntry> queue = new ArrayList<>(repository.getQueue(date, slotLabel));
        Optional<QueueEntry> target = queue.stream()
                .filter(e -> e.getNumber() == number)
                .findFirst();
        target.ifPresent(entry -> entry.setStatus(QueueStatus.SKIPPED));
        repository.replaceQueue(date, slotLabel, queue);
        return target;
    }

    public Optional<QueueEntry> serve(LocalDate date, String slotLabel, int number) {
        List<QueueEntry> queue = new ArrayList<>(repository.getQueue(date, slotLabel));
        Optional<QueueEntry> target = queue.stream()
                .filter(e -> e.getNumber() == number)
                .findFirst();
        target.ifPresent(entry -> entry.setStatus(QueueStatus.SERVED));
        repository.replaceQueue(date, slotLabel, queue);
        return target;
    }
}
