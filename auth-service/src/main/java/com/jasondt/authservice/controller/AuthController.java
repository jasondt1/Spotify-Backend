package com.jasondt.authservice.controller;

import com.jasondt.authservice.dto.*;
import com.jasondt.authservice.exception.InvalidTokenException;
import com.jasondt.authservice.service.AuthService;
import com.jasondt.authservice.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody RegisterRequestDto request) {
        String token = authService.register(request.getEmail(), request.getPassword(), request.getName(), request.getBirthday(), request.getGender());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(new TokenResponseDto(token), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<UserInfoDto> validateToken(@CookieValue(name = "jwt", required = false) String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ChangePasswordRequestDto request
    ) {
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }


}