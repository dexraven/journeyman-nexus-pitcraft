package com.journeyman.nexus.pitcraft.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "meat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private MeatType meatType;

    private double weightInLbs;

    // --- The Schedule (Mutable via SMS) ---
    private LocalDateTime prepTime;
    private LocalDateTime fireTime;
    private LocalDateTime servingTime;

    // --- State Management ---
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CookStatus status = CookStatus.PLANNED;

    private boolean alertSent; // Prevents spamming SMS

    @Column(length = 2000)
    private String prepInstructions;

    @Column(length = 2000)
    private String cookInstructions;
}