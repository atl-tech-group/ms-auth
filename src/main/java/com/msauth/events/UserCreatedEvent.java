package com.msauth.events;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private Long id;
    private String email;
    private LocalDate dateOfBirth;
    private Boolean status;
}


