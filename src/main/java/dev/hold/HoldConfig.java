package dev.hold;

public class HoldConfig {
    /**
     * Input priority for held keys.
     * Higher priority overrides other input sources in the same tick.
     * Defaults: AntiAFK 5000, pathfinder 7000, KillAura 8000, click 9000.
     */
    public int priority = 6000;
}
