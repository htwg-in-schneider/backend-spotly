package de.spotly.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Diese Klasse sagt der Datenbank, wie eine Kategorie gespeichert werden soll
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Die automatische ID für die Kategorie

    private String name; // Der Name der Kategorie (z.B. "Café" oder "Sport")

    // Leerer Konstruktor für die Datenbank (JPA)
    public Category() {
    }

    // Damit kann man im Code schnell eine neue Kategorie mit Namen anlegen
    public Category(String name) {
        this.name = name;
    }

    // Standard Getter und Setter für den Zugriff auf die Daten
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}