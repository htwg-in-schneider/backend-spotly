package de.spotly.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.SpotRepository;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotRepository spotRepository;

    public SpotController(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    // GET: alle Spots
    @GetMapping
    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    // POST: neuen Spot anlegen
    @PostMapping
    public Spot createSpot(@RequestBody Spot spot) {
        return spotRepository.save(spot);
    }

    // PUT: Spot aktualisieren
    @PutMapping("/{id}")
    public Spot updateSpot(@PathVariable Long id, @RequestBody Spot spotDetails) {
        Spot spot = spotRepository.findById(id).orElseThrow();

        spot.setTitle(spotDetails.getTitle());
        spot.setDescription(spotDetails.getDescription());
        spot.setCategory(spotDetails.getCategory());
        spot.setLocation(spotDetails.getLocation());
        spot.setImageUrl(spotDetails.getImageUrl());

        return spotRepository.save(spot);
    }

    // DELETE: Spot löschen
    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        spotRepository.deleteById(id);
    }
}
