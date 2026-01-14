package de.spotly.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Dieser Service wandelt Adressen automatisch in Landkarten-Koordinaten um
@Service
public class GeocodingService {

    // Wir nutzen den kostenlosen Dienst von OpenStreetMap (Nominatim)
    private final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public double[] getCoordinates(String location) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Leerzeichen in der Adresse werden für die URL passend gemacht
            String url = NOMINATIM_URL + location.replace(" ", "+");
            String response = restTemplate.getForObject(url, String.class);

            // Die Antwort vom Server (JSON) wird hier verarbeitet
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Wenn ein Ort gefunden wurde, nehmen wir die Koordinaten des ersten Treffers
            if (root.isArray() && root.size() > 0) {
                double lat = root.get(0).get("lat").asDouble();
                double lon = root.get(0).get("lon").asDouble();
                return new double[]{lat, lon}; // Gibt Breitengrad und Längengrad zurück
            }
        } catch (Exception e) {
            // Falls das Internet weg ist oder der Dienst hakt, loggen wir den Fehler
            System.err.println("Geocoding fehlgeschlagen: " + e.getMessage());
        }
        return null;
    }
}