package com.msvcuser.service.impl;

import com.msvcuser.DTOs.LoginResponseDto;
import com.msvcuser.DTOs.UserDto;
import com.msvcuser.entity.UserEntity;
import com.msvcuser.repository.UserRepository;
import com.msvcuser.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${secret-key}")
    private String secretKey;

    @Override
    @Transactional
    public LoginResponseDto processOAuthPostLogin(
        OAuth2AuthenticationToken token
    ) {
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("name");
        String picture = token.getPrincipal().getAttribute("picture");

        UserDto userDto = registerOrLoginOAuthUser(email, name, picture);

        String jwtToken = generateToken(email);

        return LoginResponseDto.builder()
            .message("Login successful")
            .user(userDto)
            .jwtToken(jwtToken)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository
            .findAll()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private UserDto registerOrLoginOAuthUser(
        String email,
        String name,
        String picture
    ) {
        UserEntity user = userRepository
            .findByEmail(email)
            .map(existingUser -> {
                existingUser.setName(name);
                existingUser.setPicture(picture);
                return userRepository.save(existingUser);
            })
            .orElseGet(() -> {
                UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .name(name)
                    .picture(picture)
                    .build();
                return userRepository.save(newUser);
            });
        return toDto(user);
    }

    private String generateToken(String email) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey signingKey = new SecretKeySpec(
            keyBytes,
            SignatureAlgorithm.HS256.getJcaName()
        );

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(
                new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)
            )
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    private UserDto toDto(UserEntity entity) {
        return UserDto.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .name(entity.getName())
            .picture(entity.getPicture())
            .build();
    }
}
