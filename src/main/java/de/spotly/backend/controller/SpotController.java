package de.spotly.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
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
import de.spotly.backend.service.SpotService;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;

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
    public ResponseEntity<Map<String, Object>> createSpot(@RequestBody Spot spot) {
        Spot saved = spotService.save(spot);
        return ResponseEntity.status(201).body(mapToFrontend(saved));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateSpot(@PathVariable Long id, @RequestBody Spot spotDetails) {
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
