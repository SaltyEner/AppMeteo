package com.example.demo.service;

import com.example.demo.model.WeatherMeasurement;
import com.example.demo.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeteoServiceTest {

    @Mock
    private WeatherRepository repository;

    @InjectMocks
    private MeteoService meteoService;

    @BeforeEach
    void setupRepoVuoto() {
        // Default: ogni città restituisce lista vuota se non specificato altrimenti nel test
        when(repository.findByCity(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new ArrayList<>());
    }

    @Test
    void calcolaMedie_restituisceMediaCorrettaConUnitaDiMisura() {
        when(repository.findByCity("Roma")).thenReturn(List.of(
                misurazione("Roma", 20.0, 10.0),
                misurazione("Roma", 22.0, 14.0),
                misurazione("Roma", 24.0, 18.0)
        ));

        List<Map<String, Object>> risultati = meteoService.calcolaMedie();

        Map<String, Object> roma = risultati.stream()
                .filter(r -> "Roma".equals(r.get("citta")))
                .findFirst()
                .orElseThrow();

        assertThat(roma.get("media_temperatura")).isEqualTo(22.0);
        assertThat(roma.get("media_vento")).isEqualTo(14.0);
        assertThat(roma.get("unita_temperatura")).isEqualTo("°C");
        assertThat(roma.get("unita_vento")).isEqualTo("km/h");
    }

    @Test
    void calcolaMedie_ignoraCittaSenzaMisurazioni() {
        when(repository.findByCity("Milano")).thenReturn(List.of(
                misurazione("Milano", 15.0, 5.0)
        ));

        List<Map<String, Object>> risultati = meteoService.calcolaMedie();

        assertThat(risultati).hasSize(1);
        assertThat(risultati.get(0).get("citta")).isEqualTo("Milano");
    }

    @Test
    void calcolaMedie_arrotondaA2Decimali() {
        when(repository.findByCity("Roma")).thenReturn(List.of(
                misurazione("Roma", 20.123, 10.456),
                misurazione("Roma", 20.456, 10.123)
        ));

        List<Map<String, Object>> risultati = meteoService.calcolaMedie();

        Map<String, Object> roma = risultati.get(0);
        assertThat((double) roma.get("media_temperatura")).isEqualTo(20.29);
        assertThat((double) roma.get("media_vento")).isEqualTo(10.29);
    }

    @Test
    void calcolaMedie_usaUnitaSalvateNelDb() {
        WeatherMeasurement m = misurazione("Napoli", 25.0, 12.0);
        m.setTemperatureUnit("K");
        m.setWindspeedUnit("m/s");
        when(repository.findByCity("Napoli")).thenReturn(List.of(m));

        List<Map<String, Object>> risultati = meteoService.calcolaMedie();
        Map<String, Object> napoli = risultati.get(0);

        assertThat(napoli.get("unita_temperatura")).isEqualTo("K");
        assertThat(napoli.get("unita_vento")).isEqualTo("m/s");
    }

    private WeatherMeasurement misurazione(String citta, double temp, double vento) {
        WeatherMeasurement m = new WeatherMeasurement();
        m.setCity(citta);
        m.setTemperature(temp);
        m.setWindspeed(vento);
        m.setTemperatureUnit("°C");
        m.setWindspeedUnit("km/h");
        m.setTimestamp(LocalDateTime.now());
        return m;
    }
}
