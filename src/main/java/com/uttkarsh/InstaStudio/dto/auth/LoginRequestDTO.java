package com.uttkarsh.InstaStudio.dto.auth;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequestDTO {
    public String firebaseToken;
    public UserType loginType;
}
