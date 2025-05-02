package com.uttkarsh.InstaStudio.dto;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileCompletionRequestDTO {

    private String firebaseId;

    private String userName;

    private String userPhoneNo;

    private String userEmail;

    private UserType userType;

}
