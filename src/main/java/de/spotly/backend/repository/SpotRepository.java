package de.spotly.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.spotly.backend.entity.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    List<Spot> findByTitleContainingIgnoreCase(String title);

    List<Spot> findByCategoryIgnoreCase(String category);

    List<Spot> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String title, String category);

    List<Spot> findByOwnerId(String ownerId);
}
