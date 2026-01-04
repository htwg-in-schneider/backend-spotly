package de.spotly.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    private final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public double[] getCoordinates(String location) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Wir fragen die OpenStreetMap API ab
            String url = NOMINATIM_URL + location.replace(" ", "+");
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.isArray() && root.size() > 0) {
                double lat = root.get(0).get("lat").asDouble();
                double lon = root.get(0).get("lon").asDouble();
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.err.println("Geocoding fehlgeschlagen: " + e.getMessage());
        }
        return null; // Falls nichts gefunden wurde
    }
}