package com.jasondt.authservice.controller;

import com.jasondt.authservice.dto.AuthRequestDto;
import com.jasondt.authservice.dto.TokenResponseDto;
import com.jasondt.authservice.dto.UserInfoDto;
import com.jasondt.authservice.util.CookieUtil;
import com.jasondt.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<TokenResponseDto> register(@RequestBody AuthRequestDto request) {
        String token = authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody AuthRequestDto request, HttpServletResponse response) {
        String token = authService.login(request.getUsername(), request.getPassword());
        response.addCookie(CookieUtil.createJwtCookie(token));
        return new ResponseEntity<>(new TokenResponseDto(token), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<UserInfoDto> validateToken(@CookieValue(name = "jwt", required = false) String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }
}