package com.jasondt.authservice.service;

import com.jasondt.authservice.event.EventProducer;
import com.jasondt.authservice.event.UserRegisteredEvent;
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

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EventProducer eventProducer;

    public String register(String email, String password, String name, Date birthday, String gender) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UsernameAlreadyExistsException("This email is already registered. Please use another one.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("user");

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save user to database", e);
        }

        try {
            UserRegisteredEvent event = UserRegisteredEvent.builder()
                    .userId(user.getId())
                    .username(email)
                    .name(name)
                    .birthday(birthday)
                    .gender(gender)
                    .build();

            eventProducer.sendUserRegisteredEvent(event);
        } catch (Exception e) {
            throw new DatabaseException("Failed to publish user registered event", e);
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }



    public String login(String username, String password) {
        User user;
        try {
            user = userRepository.findByEmail(username)
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

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update password", e);
        }
    }

}