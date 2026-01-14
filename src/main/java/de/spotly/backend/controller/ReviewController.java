package de.spotly.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public ReviewController(ReviewRepository reviewRepository, SpotRepository spotRepository) {
        this.reviewRepository = reviewRepository;
        this.spotRepository = spotRepository;
    }

    // Erstellt eine neue Bewertung für einen bestimmten Spot
    @PostMapping
    public Review createReview(@RequestBody ReviewDTO dto) {
        // Zuerst prüfen, ob der Spot überhaupt existiert
        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new RuntimeException("Spot not found"));

        // Daten aus dem DTO in das echte Review-Objekt übertragen
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setSpot(spot);

        return reviewRepository.save(review);
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