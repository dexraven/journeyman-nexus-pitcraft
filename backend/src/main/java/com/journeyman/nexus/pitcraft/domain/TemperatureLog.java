package com.journeyman.nexus.pitcraft.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class TemperatureLog {

    @Id
    private String id;

    private double degreesFahrenheit;

    private LocalDateTime logTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MeatSession meatSession;

    // Custom constructor for easy creation
    public TemperatureLog(MeatSession session, double degrees) {
        this.id = UUID.randomUUID().toString();
        this.meatSession = session;
        this.degreesFahrenheit = degrees;
        this.logTime = LocalDateTime.now();
    }
}