package com.journeyman.nexus.pitcraft.controller;

import com.journeyman.nexus.pitcraft.domain.MeatSession;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import com.journeyman.nexus.pitcraft.service.MeatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meat")
@RequiredArgsConstructor
public class MeatController {

    private final MeatService meatService;

    @PostMapping("/plan")
    public ResponseEntity<MeatSession> addMeat(@RequestBody MeatRequest request) {
        return ResponseEntity.ok(meatService.createSession(request));
    }

    @GetMapping("/active")
    public ResponseEntity<List<MeatSession>> getActive() {
        return ResponseEntity.ok(meatService.getActiveSessions());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelMeat(@PathVariable String id) {
        try {
            meatService.cancelSession(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // Return 409 CONFLICT with the specific error message
            return ResponseEntity.status(409).body(java.util.Map.of(
                    "error", "CANCELLATION_DENIED",
                    "message", e.getMessage()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}