package com.example.demo.controller;

import com.example.demo.service.MeteoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeteoController {

    private final MeteoService meteoService;

    public MeteoController(MeteoService meteoService) {
        this.meteoService = meteoService;
    }

    @GetMapping("/meteo/medie")
    public List<Map<String, Object>> getMedieCitta() {
        return meteoService.calcolaMedie();
    }
}