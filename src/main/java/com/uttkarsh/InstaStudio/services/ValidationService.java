package com.uttkarsh.InstaStudio.services;

import org.springframework.stereotype.Service;

@Service
public interface ValidationService {

    void isStudioValid(Long studioId);

    void isMemberValid(Long studioId, Long memberId);
}
