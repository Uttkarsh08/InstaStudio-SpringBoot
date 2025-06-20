package com.uttkarsh.InstaStudio.dto.user;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDTO {

    private Long userId;

    private String firebaseId;

    private String userName;

    private String userEmail;

    private String userPhoneNo;

    private UserType userType;

    private LocalDateTime registrationDate;

}
