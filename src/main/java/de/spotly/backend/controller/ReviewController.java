package de.spotly.backend.controller;

import java.util.List;

import de.spotly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import de.spotly.backend.dto.ReviewDTO;
import de.spotly.backend.entity.Review;
import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.ReviewRepository;
import de.spotly.backend.repository.SpotRepository;

// Dieser Controller regelt alles rund um die Nutzerbewertungen
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, SpotRepository spotRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.spotRepository = spotRepository;
        this.userRepository = userRepository;
    }

    // Erstellt eine neue Bewertung für einen bestimmten Spot
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO dto, @AuthenticationPrincipal Jwt jwt) { // Hier nutzen wir jetzt Jwt wie im SpotController
            String subjectId = jwt.getSubject();

            // Check, ob der User gesperrt ist (wie im SpotController)
            boolean isBlocked = userRepository.findByOauthId(subjectId)
                    .map(user -> !user.isEnabled())
                    .orElse(false);

            if (isBlocked) {
                return ResponseEntity.status(403).body("Sie wurden gesperrt.");
            }
        // Zuerst prüfen, ob der Spot überhaupt existiert
        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new RuntimeException("Spot not found"));

        // Daten aus dem DTO in das echte Review-Objekt übertragen
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setSpot(spot);

        return ResponseEntity.ok(reviewRepository.save(review));
    }

    // Zeigt alle Bewertungen an, die zu einem bestimmten Spot gehören
    @GetMapping("/spot/{spotId}")
    public List<Review> getReviewsBySpot(@PathVariable Long spotId) {
        return reviewRepository.findBySpotId(spotId);
    }

    // Löscht eine Bewertung anhand ihrer ID
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
    }
}