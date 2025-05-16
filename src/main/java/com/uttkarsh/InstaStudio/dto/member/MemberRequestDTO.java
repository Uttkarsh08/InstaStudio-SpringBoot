package com.uttkarsh.InstaStudio.dto.member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberRequestDTO {

    @NotBlank(message = "Member email is required")
    @Email(message = "Member email must be valid")
    private String memberEmail;

    @NotNull(message = "Member mail is required")
    @Positive(message = "Member salary must be positive")
    private Long salary;

    @NotBlank(message = "Member specialization is required")
    @Size(max = 20, message = "Specialization must be less than 100 characters")
    private String specialization;

    @NotNull(message = "Studio Id is required")
    @Positive(message = "Studio Id must be positive")
    private Long studioId;
}
