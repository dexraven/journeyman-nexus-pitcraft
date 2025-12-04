package com.journeyman.nexus.pitcraft.strategy;

import com.journeyman.nexus.pitcraft.domain.MeatType;
import com.journeyman.nexus.pitcraft.dto.MeatRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlowSmokeStrategy extends BaseMeatStrategy {

    @Override
    public boolean supports(MeatType type) {
        return type == MeatType.BEEF_BRISKET;
    }

    @Override
    protected ActivePhase calculateActivePhase(MeatRequest request) {
        // 12h Smoke + 12h Rest = 24h Active
        return ActivePhase.builder()
                .cookHours(12.0)
                .restHours(12.0)
                .instructions("Smoke until probe tender (~203F), then Heated Rest for 12 hours.")
                .build();
    }

    @Override
    protected List<String> getIngredients(MeatRequest request) {
        return List.of(
                "Full Packer Brisket", "Coarse Black Pepper (16 mesh)",
                "Kosher Salt", "Pink Butcher Paper", "Post Oak Wood Splits"
        );
    }
}