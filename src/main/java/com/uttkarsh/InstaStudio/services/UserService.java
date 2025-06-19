package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.user.UserProfileResponseDTO;
import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.dto.user.UserResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import org.springframework.stereotype.Service;

@Service
public interface UserService {


    UserResponseDTO createUser(UserRequestDTO requestDTO);

    boolean existsByFirebaseId(String firebaseId);

    User getUserByFirebaseId(String firebaseId);

    User getUserByFirebaseIdAndUserType(String firebaseId, UserType userType);

    UserProfileResponseDTO getUserProfile(String firebaseId);
}
