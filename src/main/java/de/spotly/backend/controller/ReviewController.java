package de.spotly.backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import de.spotly.backend.dto.ReviewDTO;
import de.spotly.backend.entity.Review;
import de.spotly.backend.service.ReviewService;
import de.spotly.backend.repository.ReviewRepository;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    // Erstellt eine neue Bewertung über den Service
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO dto, @AuthenticationPrincipal Jwt jwt) {
        try {
            // Die gesamte Logik (Check, Save, Stats-Update) passiert jetzt hier:
            Review savedReview = reviewService.addReviewAndUpdateSpot(dto, jwt.getSubject());
            return ResponseEntity.ok(savedReview);
        } catch (RuntimeException e) {
            // Falls der Service "USER_BLOCKED" wirft
            if ("USER_BLOCKED".equals(e.getMessage())) {
                return ResponseEntity.status(403).body("Ihr Account wurde gesperrt.");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Abrufen bleibt simpel
    @GetMapping("/spot/{spotId}")
    public List<Review> getReviewsBySpot(@PathVariable Long spotId) {
        return reviewRepository.findBySpotId(spotId);
    }

    // Löschen ebenfalls über den Service (wegen der Statistik-Neuberechnung)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReviewAndUpdateSpot(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}