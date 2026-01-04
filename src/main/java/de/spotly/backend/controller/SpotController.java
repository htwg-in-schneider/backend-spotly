package de.spotly.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // Neu

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // Neu
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.spotly.backend.entity.Spot;
import de.spotly.backend.service.SpotService;
import de.spotly.backend.service.GeocodingService; // Neu

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;

    @Autowired // Neu: Service für die Koordinaten-Berechnung
    private GeocodingService geocodingService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllSpots(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category
    ) {
        List<Spot> spots = spotService.findSpotsByCriteria(title, category);
        return spots.stream()
                .map(this::mapToFrontend)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getSpotById(@PathVariable Long id) {
        Spot spot = spotService.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden: " + id));
        return mapToFrontend(spot);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSpot(@Valid @RequestBody Spot spot) {
        // AUTOMATIK: Wenn das Frontend keine Koordinaten sendet, Adresse umwandeln
        if (spot.getLatitude() == null || spot.getLongitude() == null) {
            double[] coords = geocodingService.getCoordinates(spot.getLocation());
            if (coords != null) {
                spot.setLatitude(coords[0]);
                spot.setLongitude(coords[1]);
            }
        }

        Spot saved = spotService.save(spot);
        return ResponseEntity.status(201).body(mapToFrontend(saved));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateSpot(@PathVariable Long id, @Valid @RequestBody Spot spotDetails) {
        // Auch beim Update prüfen, ob sich die Adresse geändert hat und Koordinaten neu berechnen
        double[] coords = geocodingService.getCoordinates(spotDetails.getLocation());
        if (coords != null) {
            spotDetails.setLatitude(coords[0]);
            spotDetails.setLongitude(coords[1]);
        }

        Spot saved = spotService.update(id, spotDetails);
        return mapToFrontend(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        spotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> mapToFrontend(Spot s) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("title", s.getTitle());
        map.put("description", s.getDescription());
        map.put("imageUrl", s.getImageUrl());
        map.put("location", s.getLocation());

        // WICHTIG: Koordinaten für die Karte ans Frontend mitschicken
        map.put("latitude", s.getLatitude());
        map.put("longitude", s.getLongitude());

        List<Map<String, Object>> reviewMaps = s.getReviews().stream()
                .map(r -> {
                    Map<String, Object> reviewMap = new HashMap<>();
                    reviewMap.put("id", r.getId());
                    reviewMap.put("rating", r.getRating());
                    reviewMap.put("comment", r.getComment());
                    return reviewMap;
                })
                .collect(Collectors.toList());
        map.put("reviews", reviewMaps);

        Map<String, String> category = new HashMap<>();
        category.put("name", s.getCategory());
        map.put("category", category);

        return map;
    }
}
