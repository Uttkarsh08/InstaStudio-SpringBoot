package com.uttkarsh.InstaStudio.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenRefreshRequestDTO {

    @NotBlank(message = "Refresh token can't be blank")
    private String refreshToken;
}