package com.uttkarsh.InstaStudio.dto.event;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestDTO {

    @NotBlank(message = "Client name can't be blank")
    @Size(max = 20, message = "Client name must be at most 20 characters")
    private String clientName;

    @NotBlank(message = "Client number can't be blank")
    @Pattern(regexp = "\\d{10}", message = "Client phone number must be exactly 10 digits")
    private String clientPhoneNo;

    @NotBlank(message = "Event type can't be blank")
    @Size(max = 20, message = "Event type must be at most 20 characters")
    private String eventType;

    @NotNull(message = "Event start date is required")
    @Future(message = "Event start date must be in the future")
    private LocalDateTime eventStartDate;

    @NotNull(message = "Event end date is required")
    @Future(message = "Event end date must be in the future")
    private LocalDateTime eventEndDate;

    @NotBlank(message = "Event location can't be blank")
    @Size(max = 20, message = "Event location must be at most 20 characters")
    private String eventLocation;

    @NotBlank(message = "Event city can't be blank")
    @Size(max = 20, message = "Event city must be at most 20 characters")
    private String eventCity;

    @NotBlank(message = "Event state can't be blank")
    @Size(max = 20, message = "Event state must be at most 20 characters")
    private String eventState;

    @NotNull(message = "Must specify the event save state")
    private boolean evenIsSaved;

    @NotNull(message = "Studio Id is required")
    @Positive(message = "Studio Id must be positive")
    private Long studioId;

    private Set<Long> subEventsIds;

    private Set<Long> memberIds;

    private Set<Long> resourceIds;
}

