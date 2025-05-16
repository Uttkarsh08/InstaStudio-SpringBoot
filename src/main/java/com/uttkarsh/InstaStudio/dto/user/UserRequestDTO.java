package com.uttkarsh.InstaStudio.dto.user;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    @NotBlank(message = "Firebase ID can't be blank")
    private String firebaseId;

    @NotBlank(message = "User name can't be blank")
    @Size(max = 50, message = "User name must be at most 50 characters")
    private String userName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "User email can't be blank")
    private String userEmail;

    @Pattern(regexp = "\\d{10}", message = "User phone number must be exactly 10 digits")
    private String userPhoneNo;

    @NotNull(message = "User type can't be null")
    private UserType userType;
}
