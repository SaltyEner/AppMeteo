package com.example.demo.repository;

import com.example.demo.model.WeatherMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeatherRepository extends JpaRepository<WeatherMeasurement, Long> {
    // Ci servirà per prendere tutte le misurazioni di una singola città per fare la media
    List<WeatherMeasurement> findByCity(String city);
}