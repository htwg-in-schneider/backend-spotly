package de.spotly.backend.service;

import de.spotly.backend.dto.ReviewDTO;
import de.spotly.backend.entity.Review;
import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.ReviewRepository;
import de.spotly.backend.repository.SpotRepository;
import de.spotly.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SpotRepository spotRepository;
    private final AdminLogService adminLogService;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, SpotRepository spotRepository,
                         AdminLogService adminLogService, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.spotRepository = spotRepository;
        this.adminLogService = adminLogService;
        this.userRepository = userRepository;
    }


    @Transactional
    public Review addReviewAndUpdateSpot(ReviewDTO dto, String subjectId) {

        boolean isBlocked = userRepository.findByOauthId(subjectId)
                .map(user -> !user.isEnabled())
                .orElse(false);

        if (isBlocked) {
            throw new RuntimeException("USER_BLOCKED");
        }


        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden"));


        Review newReview = new Review();
        newReview.setRating(dto.getRating());
        newReview.setComment(dto.getComment());
        newReview.setSpot(spot);


        Review savedReview = reviewRepository.save(newReview);


        recalculateSpotStatistics(spot);


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