package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.ProfileCompletionRequestDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User getUserByFirebaseIdAndUserType(String firebaseId, UserType type){
        return userRepository.getUserByFirebaseIdAndUserType(firebaseId, type);
    }

    public User createUser(ProfileCompletionRequestDTO requestDTO) {
        User userToSave = modelMapper.map(requestDTO, User.class);
        return userRepository.save(userToSave);
    }

    public boolean existsByFirebaseIdAndUserType(String firebaseId, UserType userType) {
        return userRepository.existsByFirebaseIdAndUserType(firebaseId, userType);
    }
}
