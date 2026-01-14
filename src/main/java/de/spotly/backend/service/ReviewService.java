package de.spotly.backend.service;

import de.spotly.backend.entity.Review;
import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.ReviewRepository;
import de.spotly.backend.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public Review addReviewAndUpdateSpot(Long spotId, Review newReview) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden"));

        newReview.setSpot(spot);
        Review savedReview = reviewRepository.save(newReview);

        double currentTotal = (spot.getAverageRating() != null ? spot.getAverageRating() : 0.0)
                * (spot.getReviewCount() != null ? spot.getReviewCount() : 0);

        int newCount = (spot.getReviewCount() != null ? spot.getReviewCount() : 0) + 1;
        double newAverage = (currentTotal + newReview.getRating()) / newCount;

        spot.setReviewCount(newCount);
        spot.setAverageRating(newAverage);
        spotRepository.save(spot);

        adminLogService.log("SYSTEM", "REVIEW_ADDED", "Neue Bewertung für Spot '" + spot.getTitle() + "'");

        return savedReview;
    }

    @Transactional
    public void deleteReviewAndUpdateSpot(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review nicht gefunden"));

        Spot spot = review.getSpot();
        reviewRepository.delete(review);

        recalculateSpotStatistics(spot);
        adminLogService.log("SYSTEM", "REVIEW_DELETED", "Bewertung ID " + reviewId + " wurde gelöscht.");
    }

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