package com.akatsuki.base55.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow public access to your controller path (assuming it's /api/v1/base55)
                        .requestMatchers("/api/v1/base55/**").permitAll()
                        // Require authentication for any other requests (if you have others)
                        .anyRequest().authenticated()
                )
                // Disable CSRF if you are calling it with POST/PUT from a non-browser client like cURL or Postman
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
