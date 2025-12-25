package de.spotly.backend.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel darf nicht leer sein")
    @Size(min = 2, max = 100, message = "Titel muss zwischen 2 und 100 Zeichen lang sein")
    private String title;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    private String description;

    @NotBlank(message = "Kategorie darf nicht leer sein")
    private String category;

    @NotBlank(message = "Standort darf nicht leer sein")
    private String location;

    private String imageUrl; // Bild-URL ist oft optional, daher kein @NotBlank

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    // Standard-Konstruktor (wichtig für JPA)
    public Spot() {}

    // Konstruktor für einfaches Anlegen
    public Spot(String title, String description, String category, String location, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    // --- Getter & Setter ---

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
}