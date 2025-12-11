package com.jasondt.graphbff.client;

import com.jasondt.graphbff.dto.UserCreateDto;
import com.jasondt.graphbff.dto.UserResponseDto;
import com.jasondt.graphbff.dto.UserUpdateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class UserClient {

    private final WebClient webClient;
    private final String gatewayUrl;

    public UserClient(WebClient webClient, @Value("${gateway.url:http://localhost:8080}") String gatewayUrl) {
        this.webClient = webClient;
        this.gatewayUrl = gatewayUrl;
    }

    public Flux<UserResponseDto> getAllUsers() {
        return webClient.get()
                .uri(gatewayUrl + "/api/users")
                .retrieve()
                .bodyToFlux(UserResponseDto.class);
    }

    public Mono<UserResponseDto> getUserById(UUID id) {
        return webClient.get()
                .uri(gatewayUrl + "/api/users/" + id)
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }

    public Mono<UserResponseDto> createUser(UserCreateDto dto) {
        return webClient.post()
                .uri(gatewayUrl + "/api/users")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }

    public Mono<UserResponseDto> updateUser(UUID id, UserUpdateDto dto) {
        return webClient.put()
                .uri(gatewayUrl + "/api/users/" + id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }

    public Mono<Boolean> deleteUser(UUID id) {
        return webClient.delete()
                .uri(gatewayUrl + "/api/users/" + id)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful());
    }

    public Mono<UserResponseDto> getMe() {
        return webClient.get()
                .uri(gatewayUrl + "/api/users/me")
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }
}
