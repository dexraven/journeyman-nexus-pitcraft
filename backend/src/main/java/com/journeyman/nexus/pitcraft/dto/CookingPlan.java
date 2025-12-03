package com.journeyman.nexus.pitcraft.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CookingPlan {
    private LocalDateTime prepTime;
    private LocalDateTime fireTime;
    private LocalDateTime servingTime;

    private String prepInstructions;
    private String cookInstructions;

    private double totalProcessHours;
    private List<String> shoppingList;
}