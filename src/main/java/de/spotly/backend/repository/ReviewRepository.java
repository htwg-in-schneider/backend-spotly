package de.spotly.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.spotly.backend.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySpotId(Long spotId);
}
