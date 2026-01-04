package de.spotly.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Öffentlich zugängliche Pfade
                        .requestMatchers(HttpMethod.GET, "/api/spots/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()

                        // NEU: Reviews für alle sichtbar machen
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                        // Alles andere (POST /api/reviews, POST /api/spots, etc.) erfordert Login
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}