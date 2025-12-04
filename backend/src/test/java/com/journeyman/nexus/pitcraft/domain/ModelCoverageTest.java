package com.journeyman.nexus.pitcraft.domain;

import com.journeyman.nexus.pitcraft.ai.PitCommand;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelCoverageTest {

    @Test
    void testMeatSessionBuilderAndData() {
        LocalDateTime now = LocalDateTime.now();
        MeatSession s1 = MeatSession.builder()
                .id("1")
                .meatType(MeatType.BEEF_BRISKET)
                .status(CookStatus.PLANNED)
                .alertSent(true)
                .prepTime(now)
                .build();

        MeatSession s2 = new MeatSession("1", MeatType.BEEF_BRISKET, 12.0, now, now, now, CookStatus.PLANNED, true, "Instructions", "Cook");

        // Test Equals/HashCode (Lombok @Data)
        assertNotEquals(s1, s2);
        assertNotNull(s1.toString());

        // Test Setters
        s1.setWeightInLbs(10.0);
        assertEquals(10.0, s1.getWeightInLbs());
    }

    @Test
    void testDTOs() {
        // MeatRequest
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.CHICKEN);
        req.setWeightInLbs(5.0);
        req.setDesiredServingTime(LocalDateTime.MAX);

        assertEquals(MeatType.CHICKEN, req.getType());
        assertNotNull(req.toString());

        // CookingPlan
        CookingPlan plan = CookingPlan.builder().shoppingList(List.of("Salt")).build();
        assertEquals(1, plan.getShoppingList().size());

        // PitCommand (Record)
        PitCommand cmd = new PitCommand(PitCommand.Action.EXTEND_TIME, "BRISKET", 30);
        assertEquals(PitCommand.Action.EXTEND_TIME, cmd.action());
    }
}