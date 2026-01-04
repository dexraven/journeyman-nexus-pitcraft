package com.journeyman.nexus.pitcraft.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SystemControllerTest {

    @InjectMocks
    private SystemController systemController;

    @Test
    void getStatus_ReturnsOnlineInfo() {
        // 1. Act (Direct method call)
        Map<String, Object> result = systemController.getStatus();

        // 2. Assert
        assertNotNull(result, "Response map should not be null");

        // Static values we expect
        assertEquals("Journeyman Nexus Pitcraft", result.get("system"));
        assertEquals("ONLINE", result.get("status"));

        // Dynamic values (Time and Version change, so we just check they exist)
        assertNotNull(result.get("time"), "Time field should be present");
        assertNotNull(result.get("javaVersion"), "Java Version field should be present");
    }
}