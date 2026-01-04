package com.journeyman.nexus.pitcraft.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(WeatherService.class)
@TestPropertySource(properties = {
        "weather.api-key=TEST_KEY",
        "weather.zip-code=12345"
})
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void getCurrentTemp_Success() {
        String mockResponse = """
                {
                    "main": {
                        "temp": 45.5
                    }
                }
                """;

        mockServer.expect(requestTo(org.hamcrest.Matchers.containsString("api.openweathermap.org")))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        double temp = weatherService.getCurrentTempFahrenheit();

        assertEquals(45.5, temp);
    }

    @Test
    void getCurrentTemp_ApiFailure_ReturnsFallback() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.containsString("api.openweathermap.org")))
                .andRespond(withServerError());

        double temp = weatherService.getCurrentTempFahrenheit();

        assertEquals(70.0, temp);
    }
}