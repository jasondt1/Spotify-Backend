package com.jasondt.authservice.controller;

import com.jasondt.authservice.dto.LoginRequestDto;
import com.jasondt.authservice.dto.RegisterRequestDto;
import com.jasondt.authservice.dto.TokenResponseDto;
import com.jasondt.authservice.dto.UserInfoDto;
import com.jasondt.authservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}