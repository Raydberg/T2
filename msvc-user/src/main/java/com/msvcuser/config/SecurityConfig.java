package com.msvcuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .oauth2Login(oauth2Login ->
                oauth2Login.defaultSuccessUrl("/loginSuccess", true)
            )
            .logout(logout -> logout.logoutSuccessUrl("/logout"));

        return http.build();
    }
}
