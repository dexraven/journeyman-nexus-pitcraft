package com.journeyman.nexus.pitcraft.ai;

public record PitCommand(
        Action action,
        String meatType,
        int minutesModifier
) {
    public enum Action {
        EXTEND_TIME,
        MARK_DONE,
        START_COOK,
        STATUS_CHECK,
        UNKNOWN
    }
}