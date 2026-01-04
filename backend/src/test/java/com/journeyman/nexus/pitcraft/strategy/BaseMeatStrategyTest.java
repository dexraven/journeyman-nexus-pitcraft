package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseMeatStrategyTest {

    private static class TestableStrategy extends BaseMeatStrategy {
        @Override
        protected ActivePhase calculateActivePhase(MeatRequest request, double outsideTemp) {
            // Return fixed values to make math easy to check
            return ActivePhase.builder()
                    .cookHours(5.0)
                    .restHours(1.0)
                    .instructions("Test Instructions")
                    .build();
        }

        @Override
        protected List<String> getIngredients(MeatRequest request) {
            return List.of("Salt", "Test Spice");
        }

        @Override
        public boolean supports(com.journeyman.nexus.pitcraft.domain.MeatType type) {
            return true;
        }
    }

    private final TestableStrategy strategy = new TestableStrategy();

    @Test
    void calculate_CorrectlyBacktracksTime() {
        LocalDateTime servingTime = LocalDateTime.of(2025, 1, 1, 18, 0); // 6:00 PM

        MeatRequest request = new MeatRequest();
        request.setDesiredServingTime(servingTime);

        CookingPlan plan = strategy.calculate(request, 70.0);

        // Assert Logic:
        // Cook (5h) + Rest (1h) = 6 hours Active Phase
        // Prep is fixed at 12 hours.
        // Total Time = 6 + 12 = 18 hours.

        // 1. Verify Total Hours
        assertEquals(18.0, plan.getTotalProcessHours());

        // 2. Verify Fire Time (Serving - 6 hours)
        // 18:00 - 6 = 12:00 (Noon)
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), plan.getFireTime());

        // 3. Verify Prep Time (Fire Time - 12 hours)
        // 12:00 - 12 = 00:00 (Midnight)
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), plan.getPrepTime());

        // 4. Verify Mapping
        assertEquals("Test Instructions", plan.getCookInstructions());
        assertTrue(plan.getShoppingList().contains("Test Spice"));
        assertTrue(plan.getPrepInstructions().contains("refrigerate overnight"));
    }
}