package com.example.demo.dto;

public record MeteoResponseDto(
        double latitude,
        double longitude,
        CurrentWeatherUnitsDto current_weather_units,
        CurrentWeatherDto current_weather
) {}
