package com.example.demo.controller;

import com.example.demo.service.MeteoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeteoController.class)
class MeteoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeteoService meteoService;

    @Test
    void getMedieCitta_restituisceJsonConCampiAttesi() throws Exception {
        Map<String, Object> roma = new LinkedHashMap<>();
        roma.put("citta", "Roma");
        roma.put("media_temperatura", 22.5);
        roma.put("unita_temperatura", "°C");
        roma.put("media_vento", 12.3);
        roma.put("unita_vento", "km/h");

        when(meteoService.calcolaMedie()).thenReturn(List.of(roma));

        mockMvc.perform(get("/api/meteo/medie"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].citta").value("Roma"))
                .andExpect(jsonPath("$[0].media_temperatura").value(22.5))
                .andExpect(jsonPath("$[0].unita_temperatura").value("°C"))
                .andExpect(jsonPath("$[0].media_vento").value(12.3))
                .andExpect(jsonPath("$[0].unita_vento").value("km/h"));
    }

    @Test
    void getMedieCitta_restituisceListaVuotaSeNessunDato() throws Exception {
        when(meteoService.calcolaMedie()).thenReturn(List.of());

        mockMvc.perform(get("/api/meteo/medie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
