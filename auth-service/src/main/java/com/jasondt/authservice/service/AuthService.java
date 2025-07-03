package com.jasondt.authservice.service;

import com.jasondt.authservice.client.UserClient;
import com.jasondt.authservice.dto.UserInfoDto;
import com.jasondt.authservice.exception.DatabaseException;
import com.jasondt.authservice.exception.InvalidCredentialsException;
import com.jasondt.authservice.exception.InvalidTokenException;
import com.jasondt.authservice.exception.UsernameAlreadyExistsException;
import com.jasondt.authservice.model.User;
import com.jasondt.authservice.repository.UserRepository;
import com.jasondt.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;

    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("user");

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save user to database", e);
        }

        try {
            userClient.createUser(user.getId(), user.getUsername());
        } catch (Exception e) {
            throw new DatabaseException("Failed to create user in user service", e);
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }


    public String login(String username, String password) {
        User user;
        try {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve user from database", e);
        }

        try {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new InvalidCredentialsException("Invalid credentials");
            }
        } catch (Exception e) {
            throw new DatabaseException("Password validation failed", e);
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    public UserInfoDto validateToken(String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid or missing token");
        }

        UUID userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);
        return new UserInfoDto(userId, role);
    }
}