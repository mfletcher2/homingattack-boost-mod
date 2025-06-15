package me.mfletcher.homing;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Mth;

@Config(name = "homing")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.PrefixText
    public boolean enableHoming = true;
    public boolean enableBoost = true;

    public int homingRange = 20;

    int homingSpeed = 3;

    @ConfigEntry.Gui.Excluded
    int homingTicksTimeout = 40;

    @ConfigEntry.Gui.Tooltip
    public int boostLevel = 50;

    float baseHomingDamage = 0.5f;
    float defenseHomingDamageMultiplier = 0.3f;
    float toughnessHomingDamageMultiplier = 2.5f;

    public float boostHungerDrain = 0.05F;
    public int boostXpDrain = 0;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int reticleVolume = 100;

    @Override
    public void validatePostLoad() {
        homingRange = Math.max(homingRange, 1);
        homingSpeed = Math.max(homingSpeed, 1);
        homingTicksTimeout = Math.max(homingTicksTimeout, 1);
        boostLevel = Mth.clamp(boostLevel, 0, 255);
        baseHomingDamage = Math.max(baseHomingDamage, 0);
        defenseHomingDamageMultiplier = Math.max(defenseHomingDamageMultiplier, 0);
        toughnessHomingDamageMultiplier = Math.max(toughnessHomingDamageMultiplier, 0);
        boostHungerDrain = Math.max(boostHungerDrain, 0);
        boostXpDrain = Math.max(boostXpDrain, 0);
        reticleVolume = Mth.clamp(reticleVolume, 0, 100);
    }
}
