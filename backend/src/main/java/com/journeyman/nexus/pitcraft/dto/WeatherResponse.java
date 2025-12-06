package com.journeyman.nexus.pitcraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Maps: { "main": { "temp": 45.5 } }
public record WeatherResponse(Main main) {
    public record Main(@JsonProperty("temp") double temp) {}
}