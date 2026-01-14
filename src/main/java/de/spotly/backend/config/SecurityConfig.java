package de.spotly.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Hier legen wir fest, wer was darf und wie der Login geprüft wird
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Brauchen wir nicht, weil wir keine klassischen Sessions nutzen
                .cors(Customizer.withDefaults()) // Erlaubt die Verbindung zum Frontend
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Server merkt sich keine User-Sessions
                )
                .authorizeHttpRequests(auth -> auth
                        // Diese Sachen darf jeder sehen, auch ohne eingeloggt zu sein:
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/category", "/api/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/spots", "/api/spots/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews", "/api/reviews/**").permitAll()

                        // Alles andere (Spot erstellen, User sperren etc.) geht nur mit Login:
                        .anyRequest().authenticated()
                )
                // Hier sagen wir Spring, dass Auth0 die Logins per JWT verwaltet
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // Das hier ist wichtig für die Verbindung zwischen Frontend (GitHub) und Backend (Render)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Nur diese Seiten dürfen auf die API zugreifen:
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://htwg-in-schneider.github.io"
        ));
        // Erlaubte Aktionen (Lesen, Schreiben, Löschen, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Origin", "Accept", "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}