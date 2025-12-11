package com.jasondt.graphbff.graphql;

import com.jasondt.graphbff.client.UserClient;
import com.jasondt.graphbff.dto.UserCreateDto;
import com.jasondt.graphbff.dto.UserResponseDto;
import com.jasondt.graphbff.dto.UserUpdateDto;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserGraphQLController {

    private final UserClient userClient;

    @QueryMapping
    public Flux<UserResponseDto> users() {
        return userClient.getAllUsers();
    }

    @QueryMapping
    public Mono<UserResponseDto> user(@Argument UUID id) {
        return userClient.getUserById(id);
    }

    @QueryMapping
    public Mono<UserResponseDto> me() {
        return userClient.getMe();
    }

    @MutationMapping
    public Mono<UserResponseDto> createUser(@Argument UserCreateDto input) {
        return userClient.createUser(input);
    }

    @MutationMapping
    public Mono<UserResponseDto> updateUser(@Argument UUID id, @Argument UserUpdateDto input) {
        return userClient.updateUser(id, input);
    }

    @MutationMapping
    public Mono<Boolean> deleteUser(@Argument UUID id) {
        return userClient.deleteUser(id);
    }
}
