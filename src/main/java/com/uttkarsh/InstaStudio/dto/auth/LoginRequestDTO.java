package com.uttkarsh.InstaStudio.dto.auth;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Firebase token can't be blank")
    private String firebaseToken;

    @NotNull(message = "loginType can't be null")
    private UserType loginType;
}