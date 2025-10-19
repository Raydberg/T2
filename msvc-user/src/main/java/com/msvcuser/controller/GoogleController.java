package com.msvcuser.controller;

import com.msvcuser.DTOs.LoginResponseDto;
import com.msvcuser.DTOs.UserDto;
import com.msvcuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoogleController {

    private final UserService userService;

    @GetMapping("/loginSuccess")
    public ResponseEntity<LoginResponseDto> loginSuccess(OAuth2AuthenticationToken token) {
        LoginResponseDto response = userService.processOAuthPostLogin(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/")
    public String noAuth() {
        return "Endpoint público sin autenticación.";
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
