package com.jasondt.userservice.event;

import com.jasondt.userservice.dto.UserCreateDto;
import com.jasondt.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final UserService userService;

    @Bean
    public Consumer<UserRegisteredEvent> userRegistered() {
        return event -> {
            log.info("Received UserRegisteredEvent for userId: {}", event.getUserId());
            
            try {
                if (userService.existsById(event.getUserId())) {
                    log.warn("User with ID {} already exists. Skipping event {}", event.getUserId(), event.getEventId());
                    return;
                }

                UserCreateDto dto = new UserCreateDto();
                dto.setUserId(event.getUserId());
                dto.setName(event.getName());
                dto.setBirthday(event.getBirthday());
                dto.setGender(event.getGender());
                userService.createUser(dto);
            } catch (Exception e) {
                log.error("Error processing UserRegisteredEvent for userId: {}. Cause: {}", event.getUserId(), e.getCause(), e);
                throw e; // Throw to trigger DLQ/Retry
            }
        };
    }
}
