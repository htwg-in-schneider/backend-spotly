package de.spotly.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    @PostMapping
    public Spot createSpot(@RequestBody Spot spot) {
        return spotRepository.save(spot);
    }

    @GetMapping("/mine")
    public List<Spot> getMySpots(@RequestParam String user) {
        return spotRepository.findByCreatedBy(user);
    }

    @PutMapping("/{id}")
    public Spot updateSpot(@PathVariable Long id, @RequestBody Spot spotDetails) {
        Spot spot = spotRepository.findById(id).orElseThrow();
        spot.setName(spotDetails.getName());
        spot.setDescription(spotDetails.getDescription());
        spot.setLocation(spotDetails.getLocation());
        return spotRepository.save(spot);
    }

    @DeleteMapping("/{id}")
    public void deleteSpot(@PathVariable Long id) {
        spotRepository.deleteById(id);
    }
}
