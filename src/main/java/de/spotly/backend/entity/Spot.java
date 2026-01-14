package de.spotly.backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

// Diese Klasse definiert, wie ein "Spot" (ein Ort) in der Datenbank gespeichert wird
@Entity
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp // Speichert automatisch das Datum, an dem der Spot erstellt wurde
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String title;
    private String description;
    private String category;
    private String location; // Textuelle Adresse
    private String imageUrl;
    private Double latitude; // Breitengrad für die Karte
    private Double longitude; // Längengrad für die Karte
    private String ownerId; // Die ID des Users, dem dieser Spot gehört (von Auth0)
    private Double averageRating = 0.0; // Durchschnitt der Sterne
    private Integer reviewCount = 0; // Anzahl der Bewertungen insgesamt

    // Verbindung zu den Bewertungen: Ein Spot kann viele Reviews haben (One-to-Many)
    // Wenn ein Spot gelöscht wird, werden alle seine Reviews mitgelöscht (CascadeType.ALL)
    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Sorgt dafür, dass die Bewertungen im JSON mit ausgegeben werden
    private List<Review> reviews = new ArrayList<>();

    public Spot() {
    }

    // Konstruktor zum schnellen Erstellen eines neuen Objekts
    public Spot(String title, String description, String category, String location, String imageUrl, double latitude, double longitude, String ownerID, Double averageRating, Integer reviewCount) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerId = ownerID;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    // Getter und Setter, damit das System die Daten lesen und bearbeiten kann
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}