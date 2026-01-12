package de.spotly.backend.service;

import de.spotly.backend.entity.Review;
import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.ReviewRepository;
import de.spotly.backend.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SpotRepository spotRepository;

    public ReviewService(ReviewRepository reviewRepository, SpotRepository spotRepository) {
        this.reviewRepository = reviewRepository;
        this.spotRepository = spotRepository;
    }


    @Transactional
    public Review addReviewAndUpdateSpot(Long spotId, Review newReview) {
        // 1. Spot laden
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden"));

        // 2. Review mit Spot verknüpfen und speichern
        newReview.setSpot(spot);
        Review savedReview = reviewRepository.save(newReview);

        // 3. Komplexe Logik: Durchschnitt neu berechnen
        // Formel: ((Alter Schnitt * Alte Anzahl) + Neues Rating) / (Neue Anzahl)
        double currentTotal = (spot.getAverageRating() != null ? spot.getAverageRating() : 0.0)
                * (spot.getReviewCount() != null ? spot.getReviewCount() : 0);

        int newCount = (spot.getReviewCount() != null ? spot.getReviewCount() : 0) + 1;
        double newAverage = (currentTotal + newReview.getRating()) / newCount;

        // 4. Spot mit neuen Werten aktualisieren
        spot.setReviewCount(newCount);
        spot.setAverageRating(newAverage);
        spotRepository.save(spot);

        return savedReview;
    }
}