package com.uttkarsh.InstaStudio.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String userName;
    private String userEmail;
    private String firebaseId;
    private UserType userType;

    @JsonProperty("isRegistered")
    private boolean isRegistered;
}
