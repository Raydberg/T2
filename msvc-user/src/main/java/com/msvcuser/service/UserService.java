package com.msvcuser.service;


import com.msvcuser.DTOs.LoginResponseDto;
import com.msvcuser.DTOs.UserDto;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;

public interface UserService {
    LoginResponseDto processOAuthPostLogin(OAuth2AuthenticationToken token);
    List<UserDto> getAllUsers();
}
