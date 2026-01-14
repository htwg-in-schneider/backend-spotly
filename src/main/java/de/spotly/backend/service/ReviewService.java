package de.spotly.backend.service;

import de.spotly.backend.entity.Review;
import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.ReviewRepository;
import de.spotly.backend.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Dieser Service verwaltet die Bewertungen und aktualisiert die Spot-Statistiken
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SpotRepository spotRepository;
    private final AdminLogService adminLogService;

    public ReviewService(ReviewRepository reviewRepository, SpotRepository spotRepository, AdminLogService adminLogService) {
        this.reviewRepository = reviewRepository;
        this.spotRepository = spotRepository;
        this.adminLogService = adminLogService;
    }

    // Speichert ein neues Review und berechnet sofort den neuen Sterne-Durchschnitt für den Spot
    @Transactional
    public Review addReviewAndUpdateSpot(Long spotId, Review newReview) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden"));

        newReview.setSpot(spot);
        Review savedReview = reviewRepository.save(newReview);

        // Hier wird der neue Durchschnitt mathematisch ermittelt
        double currentTotal = (spot.getAverageRating() != null ? spot.getAverageRating() : 0.0)
                * (spot.getReviewCount() != null ? spot.getReviewCount() : 0);

        int newCount = (spot.getReviewCount() != null ? spot.getReviewCount() : 0) + 1;
        double newAverage = (currentTotal + newReview.getRating()) / newCount;

        spot.setReviewCount(newCount);
        spot.setAverageRating(newAverage);
        spotRepository.save(spot); // Update des Spots mit neuen Werten

        // Protokollierung im Admin-Log
        adminLogService.log("SYSTEM", "REVIEW_ADDED", "Neue Bewertung für Spot '" + spot.getTitle() + "'");

        return savedReview;
    }

    // Löscht eine Bewertung und stößt die Neuberechnung der Sterne an
    @Transactional
    public void deleteReviewAndUpdateSpot(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review nicht gefunden"));

        Spot spot = review.getSpot();
        reviewRepository.delete(review);

        // Statistiken nach dem Löschen korrigieren
        recalculateSpotStatistics(spot);
        adminLogService.log("SYSTEM", "REVIEW_DELETED", "Bewertung ID " + reviewId + " wurde gelöscht.");
    }

    // Hilfsmethode, die alle verbliebenen Reviews zählt und den Durchschnitt neu berechnet
    private void recalculateSpotStatistics(Spot spot) {
        List<Review> reviews = reviewRepository.findBySpotId(spot.getId());

        int count = reviews.size();
        double average = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        spot.setReviewCount(count);
        spot.setAverageRating(average);
        spotRepository.save(spot);
    }
}