package de.spotly.backend.dto;

/**
 * Ein Hilfsobjekt (DTO), um Bewertungsdaten vom Frontend zum Backend zu übertragen.
 * Es enthält nur die Daten, die wir beim Abschicken einer Bewertung wirklich brauchen.
 */
public class ReviewDTO {

    private int rating;   // Die Sterne-Anzahl (z.B. 1 bis 5)
    private String comment; // Der Text der Bewertung
    private Long spotId;  // Die ID des Spots, zu dem die Bewertung gehört

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

    public Long getSpotId() {
        return spotId;
    }

    public void setSpotId(Long spotId) {
        this.spotId = spotId;
    }
}