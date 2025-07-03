package com.jasondt.authservice.client;

import com.jasondt.authservice.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate, @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public void createUser(UUID userId, String username) {
        UserRequestDto request = new UserRequestDto();
        request.setUserId(userId);
        request.setUsername(username);

        restTemplate.postForEntity(userServiceUrl, request, Void.class);
    }
}