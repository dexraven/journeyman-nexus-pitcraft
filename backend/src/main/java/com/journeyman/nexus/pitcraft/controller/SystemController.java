package com.journeyman.nexus.pitcraft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
                "system", "Journeyman Nexus Pitcraft",
                "status", "ONLINE",
                "time", LocalDateTime.now().toString(),
                "javaVersion", System.getProperty("java.version")
        );
    }
}