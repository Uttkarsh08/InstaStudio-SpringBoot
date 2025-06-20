package com.uttkarsh.InstaStudio.utils.mappers.user;

import com.uttkarsh.InstaStudio.dto.user.UserResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserMapper {

    public UserResponseDTO toUserDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();

        dto.setUserId(user.getUserId());
        dto.setFirebaseId(user.getFirebaseId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserPhoneNo(user.getUserPhoneNo());
        dto.setUserType(user.getUserType());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }
}