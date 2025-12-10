package de.spotly.backend.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // Titel
    private String description; // Beschreibung
    private String category;    // Kategorie
    private String location;    // Standort
    private String imageUrl;    // Bild-URL

    @OneToMany(mappedBy = "spot")
@JsonManagedReference
private List<Review> reviews = new ArrayList<>();


    public Spot() {}

    public Spot(String title, String description, String category, String location, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<Review> getReviews() {return reviews;}
    public void setReviews(List<Review> reviews) {this.reviews = reviews;}
}
