package de.spotly.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Liefert die Liste der Kategorien an das Frontend
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    // Hier sind die festen Kategorien hinterlegt, die in der App zur Auswahl stehen
    @GetMapping
    public List<Map<String, String>> getCategories() {
        return List.of(
                Map.of("name", "Natur & Aussicht"),
                Map.of("name", "Shops & Märkte"),
                Map.of("name", "Events & Kultur"),
                Map.of("name", "Cafés & Essen"),
                Map.of("name", "Sport & Freizeit"),
                Map.of("name", "Andere")
        );
    }
}