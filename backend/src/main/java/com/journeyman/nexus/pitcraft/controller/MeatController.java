package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.service.MeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/meat")
@RequiredArgsConstructor
public class MeatController {

    private final MeatService meatService;

    @PostMapping("/plan")
    public ResponseEntity<MeatSession> addMeat(@RequestBody MeatRequest request) {
        // Keep simple validation guards if you want,
        // or let the service throw IllegalArgumentException
        if (request == null || request.getType() == null) {
            throw new IllegalArgumentException("MeatRequest and Type are required");
        }
        return ResponseEntity.ok(meatService.createSession(request));
    }

    @PostMapping("/{id}/temp")
    public ResponseEntity<Void> logTemp(@PathVariable String id, @RequestBody Map<String, Double> payload) {
        if (!payload.containsKey("degrees")) {
            throw new IllegalArgumentException("Payload must contain 'degrees'");
        }

        double degrees = payload.get("degrees");
        meatService.logTemperature(id, degrees);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<MeatSession>> getActive() {
        return ResponseEntity.ok(meatService.getActiveSessions());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMeat(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }

        meatService.cancelSession(id);

        return ResponseEntity.noContent().build();
    }
}