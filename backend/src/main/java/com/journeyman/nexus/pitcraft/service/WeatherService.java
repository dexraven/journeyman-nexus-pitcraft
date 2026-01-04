package com.journeyman.nexus.pitcraft.service;

import com.journeyman.nexus.pitcraft.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

    private final RestClient restClient;
    private final String apiKey;
    private final String zipCode;

    public WeatherService(
            RestClient.Builder builder,
            @Value("${weather.api-key}") String apiKey,
            @Value("${weather.zip-code}") String zipCode) {

        this.restClient = builder.baseUrl("https://api.openweathermap.org/data/2.5").build();
        this.apiKey = apiKey;
        this.zipCode = zipCode;
    }

    public double getCurrentTempFahrenheit() {
        if (apiKey == null || apiKey.isEmpty() || "MOCK_KEY".equals(apiKey)) {
            return 70.0; // Default to "Room Temp" if no key configured
        }

        try {
            // Call: /weather?zip={zip},us&appid={key}&units=imperial
            WeatherResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("zip", zipCode + ",us")
                            .queryParam("appid", apiKey)
                            .queryParam("units", "imperial") // Get Fahrenheit
                            .build())
                    .retrieve()
                    .body(WeatherResponse.class);

            return response != null ? response.main().temp() : 70.0;
        } catch (Exception e) {
            System.err.println("Weather API Failed: " + e.getMessage());
            return 70.0; // Fallback on error
        }
    }
}