package de.spotly.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.spotly.backend.entity.Spot;
import de.spotly.backend.repository.SpotRepository;

// Dieser Service enthält die Geschäftslogik für die Spot-Verwaltung
@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final AdminLogService adminLogService;

    public SpotService(SpotRepository spotRepository, AdminLogService adminLogService) {
        this.spotRepository = spotRepository;
        this.adminLogService = adminLogService;
    }

    // Filter-Logik: Entscheidet, ob nach Titel, Kategorie oder beidem gesucht wird
    public List<Spot> findSpotsByCriteria(String title, String category) {

        boolean hasTitle = title != null && !title.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        if (hasTitle && hasCategory) {
            return spotRepository.findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(title.trim(), category.trim());
        } else if (hasTitle) {
            return spotRepository.findByTitleContainingIgnoreCase(title.trim());
        } else if (hasCategory) {
            return spotRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            return spotRepository.findAll();
        }
    }

    // Findet einen einzelnen Spot anhand seiner ID
    public Optional<Spot> findById(Long id) {
        return spotRepository.findById(id);
    }

    // Speichert einen neuen Spot und protokolliert die Erstellung
    public Spot save(Spot spot) {
        Spot savedSpot = spotRepository.save(spot);
        adminLogService.log("SYSTEM", "SPOT_CREATED", "Spot '" + savedSpot.getTitle() + "' wurde erstellt.");
        return savedSpot;
    }

    // Aktualisiert die Daten eines bestehenden Spots
    public Spot update(Long id, Spot spotDetails) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spot nicht gefunden: " + id));

        // Übernahme der neuen Werte
        spot.setTitle(spotDetails.getTitle());
        spot.setDescription(spotDetails.getDescription());
        spot.setCategory(spotDetails.getCategory());
        spot.setLocation(spotDetails.getLocation());
        spot.setImageUrl(spotDetails.getImageUrl());
        spot.setLatitude(spotDetails.getLatitude());
        spot.setLongitude(spotDetails.getLongitude());

        Spot updatedSpot = spotRepository.save(spot);
        adminLogService.log("SYSTEM", "SPOT_UPDATED", "Spot ID " + id + " wurde aktualisiert.");
        return updatedSpot;
    }

    // Löscht einen Spot und erstellt einen Log-Eintrag darüber
    public void delete(Long id) {
        if (!spotRepository.existsById(id)) {
            throw new RuntimeException("Spot nicht gefunden: " + id);
        }
        spotRepository.deleteById(id);
        adminLogService.log("SYSTEM", "SPOT_DELETED", "Spot ID " + id + " wurde gelöscht.");
    }

    // Findet alle Spots, die einem bestimmten User gehören
    public List<Spot> findByOwnerId(String ownerId) {
        return spotRepository.findByOwnerId(ownerId);
    }
}