package com.journeyman.nexus.pitcraft.dto;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MeatRequest {
    private MeatType type;
    private double weightInLbs;
    private LocalDateTime desiredServingTime;
}