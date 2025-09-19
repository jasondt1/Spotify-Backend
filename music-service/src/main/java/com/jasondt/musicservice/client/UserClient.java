package com.jasondt.musicservice.client;

import com.jasondt.musicservice.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public UserResponseDto getUserById(UUID userId) {
        return restTemplate.getForObject(
                userServiceUrl + "/" + userId,
                UserResponseDto.class
        );
    }
}
