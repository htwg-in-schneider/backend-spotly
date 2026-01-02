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
                .csrf(csrf -> csrf.disable()) // Deaktiviert für zustandslose APIs
                .cors(Customizer.withDefaults()) // WICHTIG: Nutzt deine WebConfig!
                .authorizeHttpRequests(auth -> auth
                        // Besucher dürfen Spots und Kategorien anschauen (Folie 33)
                        .requestMatchers(HttpMethod.GET, "/api/spots/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()

                        // Alles andere (Posten, Profil abrufen, Löschen) erfordert Login
                        .anyRequest().authenticated()
                )
                // Konfiguriert das Backend als Resource Server für Auth0 Tokens (Folie 32)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}