package de.spotly.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private Spot spot;

    public Review() {}

    public Review(int rating, String comment, Spot spot) {
        this.rating = rating;
        this.comment = comment;
        this.spot = spot;
    }

    public Long getId() { return id; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Spot getSpot() { return spot; }
    public void setSpot(Spot spot) { this.spot = spot; }
}
