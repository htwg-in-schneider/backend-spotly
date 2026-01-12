package de.spotly.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// NEUE IMPORTS FÜR AUTH0
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import de.spotly.backend.entity.Spot;
import de.spotly.backend.service.SpotService;
import de.spotly.backend.service.GeocodingService;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private UserRepository userRepository;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    // 1. ÖFFENTLICH: Alle Spots laden (keine Änderung nötig)
    @GetMapping
    public List<Map<String, Object>> getAllSpots(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category
    ) {
        List<Spot> spots = spotService.findSpotsByCriteria(title, category);
        return spots.stream()
                .map(this::mapToFrontend)
                .collect(Collectors.toList());
    }

    // 2. PRIVAT: Nur "Meine Spots" (Nutzt das Token)
    @GetMapping("/me")
    public List<Map<String, Object>> getMySpots(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new RuntimeException("Nicht autorisiert: Kein Token gefunden.");
        }
        // Holt die Auth0 User-ID (sub) aus dem Token
        String userId = jwt.getSubject();

        List<Spot> spots = spotService.findByOwnerId(userId);
        return spots.stream()
                .map(this::mapToFrontend)
                .collect(Collectors.toList());
    }

    // 3. ÖFFENTLICH: Einzelnen Spot laden (keine Änderung nötig)
    @GetMapping("/{id}")
    public Map<String, Object> getSpotById(@PathVariable Long id) {
        Spot spot = spotService.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden: " + id));
        return mapToFrontend(spot);
    }

    // 4. PRIVAT: Spot erstellen (Speichert jetzt die ownerId automatisch)
    @PostMapping
    public ResponseEntity<?> createSpot(
            @Valid @RequestBody Spot spot,
            @AuthenticationPrincipal Jwt jwt) { // JWT hinzugefügt

        // Automatik: Setzt die ownerId aus dem Auth0 Token
        if (jwt != null) {
            spot.setOwnerId(jwt.getSubject());
        }

        String subjectId = jwt.getSubject();

        boolean isBlocked = userRepository.findByOauthId(subjectId)
                .map(user -> !user.isEnabled())
                .orElse(false);

        if (isBlocked) {
            return ResponseEntity.status(403).body("Ihr Account wurde gesperrt.");
        }

        if (spot.getLatitude() == null || spot.getLongitude() == null) {
            double[] coords = geocodingService.getCoordinates(spot.getLocation());
            if (coords != null) {
                spot.setLatitude(coords[0]);
                spot.setLongitude(coords[1]);
            }
        }

        Spot saved = spotService.save(spot);
        return ResponseEntity.status(201).body(mapToFrontend(saved));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateSpot(@PathVariable Long id, @Valid @RequestBody Spot spotDetails) {
        double[] coords = geocodingService.getCoordinates(spotDetails.getLocation());
        if (coords != null) {
            spotDetails.setLatitude(coords[0]);
            spotDetails.setLongitude(coords[1]);
        }

        Spot saved = spotService.update(id, spotDetails);
        return mapToFrontend(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        spotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // HILFSMETHODE: Mappt das Datenbank-Objekt für das Frontend
    private Map<String, Object> mapToFrontend(Spot s) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("title", s.getTitle());
        map.put("description", s.getDescription());
        map.put("imageUrl", s.getImageUrl());
        map.put("location", s.getLocation());
        map.put("latitude", s.getLatitude());
        map.put("longitude", s.getLongitude());
        map.put("createdAt", s.getCreatedAt());


        // WICHTIG: Damit das Frontend weiß, wem der Spot gehört
        map.put("ownerId", s.getOwnerId());

        List<Map<String, Object>> reviewMaps = s.getReviews().stream()
                .map(r -> {
                    Map<String, Object> reviewMap = new HashMap<>();
                    reviewMap.put("id", r.getId());
                    reviewMap.put("rating", r.getRating());
                    reviewMap.put("comment", r.getComment());
                    return reviewMap;
                })
                .collect(Collectors.toList());
        map.put("reviews", reviewMaps);

        Map<String, String> category = new HashMap<>();
        category.put("name", s.getCategory());
        map.put("category", category);

        String creatorName = "Unbekannter User";
        if (s.getOwnerId() != null) {
            // Wir suchen in der User-Tabelle nach der oauthId, die im Spot als ownerId gespeichert ist
            creatorName = userRepository.findByOauthId(s.getOwnerId())
                    .map(User::getUsername)
                    .orElse("Anonymer Local");
        }
        map.put("authorName", creatorName);


        return map;
    }
}