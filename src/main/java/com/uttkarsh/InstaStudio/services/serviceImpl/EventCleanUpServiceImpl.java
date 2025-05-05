package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.services.EventCleanUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCleanUpServiceImpl implements EventCleanUpService {

    private final EventRepository eventRepository;

    @Override
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOrphanedSubEvents() {
        List<Event> orphanedSubEvents = eventRepository.findAllByParentEventIsNullAndClientNameIsNull(); // This fetches sub-events with no parent
        for (Event subEvent : orphanedSubEvents) {
            eventRepository.delete(subEvent);
        }
    }
}
