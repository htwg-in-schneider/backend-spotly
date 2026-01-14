package de.spotly.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// Diese Klasse speichert die einzelnen Nutzer-Bewertungen in der Datenbank
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;     // Die Sterne-Anzahl
    private String comment; // Der eigentliche Text der Bewertung

    // Das hier ist die Verbindung: Viele Reviews gehören zu einem Spot (Many-to-One)
    @ManyToOne
    @JoinColumn(name = "spot_id") // In der Datenbank wird hier die ID des Spots gespeichert
    @JsonBackReference // Verhindert Endlosschleifen beim Laden der Daten (JSON)
    private Spot spot;

    public Review() {
    }

    public Review(int rating, String comment, Spot spot) {
        this.rating = rating;
        this.comment = comment;
        this.spot = spot;
    }

    // Standard Getter und Setter
    public Long getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }
}