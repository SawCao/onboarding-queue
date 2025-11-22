package com.example.onboardingqueue.service;

import com.example.onboardingqueue.config.AppProperties;
import com.example.onboardingqueue.model.SlotTemplate;
import com.example.onboardingqueue.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class SchedulingService {

    private final InMemoryRepository repository;
    private final AppProperties properties;

    public SchedulingService(InMemoryRepository repository, AppProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public List<SlotTemplate> getTemplates() {
        return repository.getSlotTemplates();
    }

    public void updateTemplates(List<SlotTemplate> templates) {
        repository.replaceSlotTemplates(templates);
    }

    public boolean isDateWithinRange(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate latest = today.plusWeeks(properties.getWeeksAhead());
        return !date.isBefore(today) && !date.isAfter(latest);
    }

    public Optional<SlotTemplate> findTemplateByLabel(String label) {
        return repository.getSlotTemplates().stream()
                .filter(t -> t.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }

    public Optional<SlotTemplate> findTemplateForTime(LocalTime now) {
        return repository.getSlotTemplates().stream()
                .filter(t -> !now.isBefore(t.getStartTime()) && now.isBefore(t.getEndTime()))
                .findFirst();
    }

    public AppProperties getProperties() {
        return properties;
    }
}
