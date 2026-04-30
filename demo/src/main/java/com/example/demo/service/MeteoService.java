package com.example.demo.service;

import com.example.demo.dto.MeteoResponseDto;
import com.example.demo.model.WeatherMeasurement;
import com.example.demo.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MeteoService {

    private static final Logger log = LoggerFactory.getLogger(MeteoService.class);

    private final RestClient restClient;
    private final WeatherRepository repository;

    // Scegliamo 5 città e inseriamo le loro coordinate
    private final Map<String, String> cities = Map.of(
            "Roma", "latitude=41.89&longitude=12.49",
            "Milano", "latitude=45.46&longitude=9.19",
            "Napoli", "latitude=40.85&longitude=14.26",
            "Torino", "latitude=45.07&longitude=7.68",
            "Palermo", "latitude=38.11&longitude=13.36"
    );

    public MeteoService(WeatherRepository repository) {
        this.repository = repository;
        this.restClient = RestClient.create();
    }

    // Job schedulato: parte in automatico ogni 30 secondi
    @Scheduled(fixedRate = 30000)
    public void raccogliDatiMeteo() {
        for (Map.Entry<String, String> entry : cities.entrySet()) {
            String cityName = entry.getKey();
            String coords = entry.getValue();
            String url = "https://api.open-meteo.com/v1/forecast?" + coords + "&current_weather=true";

            try {
                MeteoResponseDto response = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(MeteoResponseDto.class);

                if (response != null && response.current_weather() != null) {
                    WeatherMeasurement measurement = new WeatherMeasurement();
                    measurement.setCity(cityName);
                    measurement.setTemperature(response.current_weather().temperature());
                    measurement.setWindspeed(response.current_weather().windspeed());
                    measurement.setTimestamp(LocalDateTime.now());

                    // Unità di misura prese dalla risposta dell'API (con fallback ai default)
                    if (response.current_weather_units() != null) {
                        measurement.setTemperatureUnit(
                                Optional.ofNullable(response.current_weather_units().temperature()).orElse("°C"));
                        measurement.setWindspeedUnit(
                                Optional.ofNullable(response.current_weather_units().windspeed()).orElse("km/h"));
                    } else {
                        measurement.setTemperatureUnit("°C");
                        measurement.setWindspeedUnit("km/h");
                    }

                    repository.save(measurement);
                    log.info("Dati aggiornati per {}: {}{}, vento {}{}",
                            cityName,
                            measurement.getTemperature(), measurement.getTemperatureUnit(),
                            measurement.getWindspeed(), measurement.getWindspeedUnit());
                }
            } catch (Exception e) {
                log.error("Errore nel recupero dati per {}: {}", cityName, e.getMessage());
            }
        }
    }

    // Calcola le medie prendendo i dati salvati dal database
    public List<Map<String, Object>> calcolaMedie() {
        List<Map<String, Object>> risultati = new ArrayList<>();

        for (String city : cities.keySet()) {
            List<WeatherMeasurement> misurazioni = repository.findByCity(city);

            if (!misurazioni.isEmpty()) {
                double avgTemp = misurazioni.stream()
                        .mapToDouble(WeatherMeasurement::getTemperature).average().orElse(0.0);
                double avgWind = misurazioni.stream()
                        .mapToDouble(WeatherMeasurement::getWindspeed).average().orElse(0.0);

                // Prende le unità di misura dall'ultima misurazione disponibile
                WeatherMeasurement ultima = misurazioni.get(misurazioni.size() - 1);

                Map<String, Object> cityData = new LinkedHashMap<>();
                cityData.put("citta", city);
                cityData.put("media_temperatura", Math.round(avgTemp * 100.0) / 100.0);
                cityData.put("unita_temperatura",
                        Optional.ofNullable(ultima.getTemperatureUnit()).orElse("°C"));
                cityData.put("media_vento", Math.round(avgWind * 100.0) / 100.0);
                cityData.put("unita_vento",
                        Optional.ofNullable(ultima.getWindspeedUnit()).orElse("km/h"));

                risultati.add(cityData);
            }
        }
        return risultati;
    }
}
