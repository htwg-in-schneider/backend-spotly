package de.spotly.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.spotly.backend.entity.Review;
import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import de.spotly.backend.entity.Spot;
import de.spotly.backend.service.SpotService;
import de.spotly.backend.service.GeocodingService;

// Das hier ist die Haupt-Schnittstelle für alles, was mit den Spots zu tun hat
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

    // Liefert alle Spots zurück, wahlweise gefiltert nach Titel oder Kategorie
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

    // Zeigt nur die Spots an, die der aktuell eingeloggte Nutzer selbst erstellt hat
    @GetMapping("/me")
    public List<Map<String, Object>> getMySpots(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new RuntimeException("Nicht autorisiert: Kein Token gefunden.");
        }
        String userId = jwt.getSubject();

        List<Spot> spots = spotService.findByOwnerId(userId);
        return spots.stream()
                .map(this::mapToFrontend)
                .collect(Collectors.toList());
    }

    // Details zu einem ganz bestimmten Spot über die ID abrufen
    @GetMapping("/{id}")
    public Map<String, Object> getSpotById(@PathVariable Long id) {
        Spot spot = spotService.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden: " + id));
        return mapToFrontend(spot);
    }

    // Erstellt einen neuen Spot und prüft vorher, ob der User gesperrt ist
    @PostMapping
    public ResponseEntity<?> createSpot(
            @Valid @RequestBody Spot spot,
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt != null) {
            spot.setOwnerId(jwt.getSubject());
        }

        String subjectId = jwt.getSubject();

        // Check, ob der User überhaupt Spots erstellen darf (enabled-Flag)
        boolean isBlocked = userRepository.findByOauthId(subjectId)
                .map(user -> !user.isEnabled())
                .orElse(false);

        if (isBlocked) {
            return ResponseEntity.status(403).body("Ihr Account wurde gesperrt.");
        }

        // Falls keine Koordinaten mitgeschickt wurden, suchen wir sie automatisch über die Adresse
        if (spot.getLatitude() == null || spot.getLongitude() == null) {
            double[] coords = geocodingService.getCoordinates(spot.getLocation());
            if (coords != null) {
                spot.setLatitude(coords[0]);
                spot.setLongitude(coords[1]);
            }
        }

        spot.setAverageRating(0.0);
        spot.setReviewCount(0);


        Spot saved = spotService.save(spot);
        return ResponseEntity.status(201).body(mapToFrontend(saved));
    }

    @Autowired
    private de.spotly.backend.service.ReviewService reviewService;

    // Fügt einem Spot eine neue Bewertung hinzu und aktualisiert die Sterne
    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long id, @RequestBody Review review) {
        try {
            Review saved = reviewService.addReviewAndUpdateSpot(id, review);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Aktualisiert die Daten eines Spots (z.B. neue Beschreibung oder Ort)
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

    // Löscht einen Spot komplett aus der Datenbank
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        spotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Löscht eine einzelne Bewertung und berechnet den Durchschnitt neu
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReviewAndUpdateSpot(reviewId);
            return ResponseEntity.ok("Review gelöscht und Spot-Statistik aktualisiert.");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Fehler beim Löschen: " + e.getMessage());
        }
    }

    // Hilfsmethode, um die Daten so aufzubereiten, wie das Vue-Frontend sie braucht
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
        map.put("averageRating", s.getAverageRating() != null ? s.getAverageRating() : 0.0);
        map.put("reviewCount", s.getReviewCount() != null ? s.getReviewCount() : 0);

        map.put("ownerId", s.getOwnerId());

        // Auch die Bewertungen des Spots werden hier mit eingepackt
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

        // Sucht den Namen des Erstellers raus, damit wir "Anonymer Local" oder den echten Namen anzeigen können
        String creatorName = "Unbekannter User";
        if (s.getOwnerId() != null) {
            creatorName = userRepository.findByOauthId(s.getOwnerId())
                    .map(User::getUsername)
                    .orElse("Anonymer Local");
        }
        map.put("authorName", creatorName);


        return map;
    }
}