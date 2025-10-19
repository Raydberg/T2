package com.msvcuser.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String message;
    private UserDto user;
    private String jwtToken;
}