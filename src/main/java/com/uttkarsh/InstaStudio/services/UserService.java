package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.user.UserProfileCompletionRequestDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public User getUserByFirebaseIdAndUserType(String firebaseId, UserType type);

    public User createUser(UserProfileCompletionRequestDTO requestDTO);

    public boolean existsByFirebaseIdAndUserType(String firebaseId, UserType userType);
}
