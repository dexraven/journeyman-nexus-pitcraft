package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.CookingPlan;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeatPlanFactoryTest {

    @Test
    void generatePlan_SelectsCorrectStrategy() {
        List<CookingStrategy> strategies = List.of(
                new StandardSmokeStrategy(),
                new PoultryStrategy(),
                new SlowSmokeStrategy()
        );
        MeatPlanFactory factory = new MeatPlanFactory(strategies);

        MeatRequest brisketReq = new MeatRequest();
        brisketReq.setType(MeatType.BEEF_BRISKET);
        brisketReq.setDesiredServingTime(LocalDateTime.now());
        brisketReq.setWeightInLbs(12.0);

        CookingPlan brisketPlan = factory.generatePlan(brisketReq, 70.0);

        assertNotNull(brisketPlan);
        assertTrue(brisketPlan.getCookInstructions().contains("Heated Rest"),
                "Expected Brisket strategy instructions");

        MeatRequest chickenReq = new MeatRequest();
        chickenReq.setType(MeatType.CHICKEN);
        chickenReq.setDesiredServingTime(LocalDateTime.now());
        chickenReq.setWeightInLbs(5.0);

        CookingPlan chickenPlan = factory.generatePlan(chickenReq, 70.0);

        assertNotNull(chickenPlan);
        assertTrue(chickenPlan.getCookInstructions().contains("High Heat"),
                "Expected Poultry strategy instructions");
    }

    @Test
    void generatePlan_UnknownMeat_ThrowsException() {
        MeatPlanFactory factory = new MeatPlanFactory(List.of()); // Empty factory
        MeatRequest req = new MeatRequest();
        req.setType(MeatType.BEEF_BRISKET);

        assertThrows(IllegalArgumentException.class, () -> factory.generatePlan(req, 70.0));
    }
}