package de.spotly.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.spotly.backend.entity.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByCreatedBy(String createdBy);
}
