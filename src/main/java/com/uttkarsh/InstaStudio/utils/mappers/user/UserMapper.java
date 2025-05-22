package com.uttkarsh.InstaStudio.utils.mappers.user;

import com.uttkarsh.InstaStudio.dto.event.EventResponseDTO;
import com.uttkarsh.InstaStudio.dto.user.UserResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Resource;
import com.uttkarsh.InstaStudio.entities.User;
import org.springframework.context.annotation.Configuration;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class UserMapper {

    public UserResponseDTO toEventDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();

        dto.setUserId(user.getUserId());
        dto.setFirebaseId(user.getFirebaseId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserPhoneNo(user.getUserPhoneNo());
        dto.setUserType(user.getUserType());

        return dto;
    }
}