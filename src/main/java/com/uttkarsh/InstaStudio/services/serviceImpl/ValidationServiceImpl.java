package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.MemberRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.services.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final StudioRepository studioRepository;
    private final MemberRepository memberRepository;

    public  void isStudioValid(Long studioId){
        boolean exist = studioRepository.existsById(studioId);
        if(!exist){
            throw new ResourceNotFoundException("Can't find Studio with id: " + studioId);
        }
    }

    public  void isMemberValid(Long studioId, Long memberId){
        boolean exist = memberRepository.existsByMemberIdAndUser_Studio_StudioId(studioId, memberId);
        if(!exist){
            throw new ResourceNotFoundException("Can't find Member with id: " + memberId);
        }
    }

}
