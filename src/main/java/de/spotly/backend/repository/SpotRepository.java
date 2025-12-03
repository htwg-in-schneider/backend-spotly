package de.spotly.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.spotly.backend.entity.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}

