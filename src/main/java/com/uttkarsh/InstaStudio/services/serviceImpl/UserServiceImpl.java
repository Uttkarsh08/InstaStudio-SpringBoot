package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.UserProfileCompletionRequestDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import com.uttkarsh.InstaStudio.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User getUserByFirebaseIdAndUserType(String firebaseId, UserType type){
        return userRepository.getUserByFirebaseIdAndUserType(firebaseId, type);
    }

    public User createUser(UserProfileCompletionRequestDTO requestDTO) {
        User user = new User();
        user.setFirebaseId(requestDTO.getFirebaseId());
        user.setUserName(requestDTO.getUserName());
        user.setUserPhoneNo(requestDTO.getUserPhoneNo());
        user.setUserEmail(requestDTO.getUserEmail());
        user.setUserType(requestDTO.getUserType());
        return userRepository.save(user);
    }

    public boolean existsByFirebaseIdAndUserType(String firebaseId, UserType userType) {
        return userRepository.existsByFirebaseIdAndUserType(firebaseId, userType);
    }
}
