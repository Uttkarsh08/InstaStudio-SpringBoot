package com.uttkarsh.InstaStudio.services;

import org.springframework.stereotype.Service;

@Service
public interface EventCleanUpService {

    void cleanupOrphanedSubEvents();
}
