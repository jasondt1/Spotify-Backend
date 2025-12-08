package com.jasondt.authservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final StreamBridge streamBridge;

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Sending UserRegisteredEvent for userId: {}", event.getUserId());
        
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventVersion("v1");
        event.setTimestamp(System.currentTimeMillis());
        
        streamBridge.send("userRegistered-out-0", event);
    }
}
