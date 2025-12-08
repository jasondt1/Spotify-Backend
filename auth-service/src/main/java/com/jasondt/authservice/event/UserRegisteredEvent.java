package com.jasondt.authservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private UUID userId;
    private String username;
    private String name;
    private Date birthday;
    private String gender;

    private String eventId;
    private String eventVersion;
    private Long timestamp;
}
