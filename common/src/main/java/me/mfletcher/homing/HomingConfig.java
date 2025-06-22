package me.mfletcher.homing;

import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@IgnoreVisibility
public class HomingConfig extends Config {
    public HomingConfig() {
        super(ResourceLocation.fromNamespaceAndPath(HomingAttack.MOD_ID, "config"));
    }

    public boolean enableHoming = true;
    public boolean enableBoost = true;
    public boolean stopHomingOnCollision = false;

    @ValidatedInt.Restrict(min = 1, max = 50)
    public int homingRange = 20;

    @ValidatedInt.Restrict(min = 1, max = 10)
    int homingSpeed = 3;

    @ValidatedInt.Restrict(min = 1, max = 255)
    int homingTicksTimeout = 40;

    @ValidatedInt.Restrict(min = 0, max = 255)
    public int boostLevel = 50;

    @ValidatedFloat.Restrict(min = 0, max = 10)
    float baseHomingDamage = 0.5f;
    @ValidatedFloat.Restrict(min = 0, max = 10)
    float defenseHomingDamageMultiplier = 0.3f;
    @ValidatedFloat.Restrict(min = 0, max = 10)
    float toughnessHomingDamageMultiplier = 2.5f;

    @ValidatedFloat.Restrict(min = 0, max = 5)
    float homingHitboxAdd = 1.75f;
    @ValidatedInt.Restrict(min = 0, max = 40)
    public int homingDamageWaitTicks = 5;

    @ValidatedFloat.Restrict(min = 0, max = 1)
    public float boostHungerDrain = 0.05f;
    @ValidatedInt.Restrict(min = 0, max = 10)
    public int boostXpDrain = 0;

    @ValidatedFloat.Restrict(min = 0, max = 30)
    public float dashPanelPower = 10.0f;
    @ValidatedFloat.Restrict(min = 0, max = 30)
    public float dashRingPower = 10.0f;
    @ValidatedFloat.Restrict(min = 0, max = 30)
    public float springPower = 3.0f;

    @ValidatedInt.Restrict(min = 0, max = 100)
    public int homingVolume = 100;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int boostVolume = 80;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int dashPanelVolume = 80;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int dashRingVolume = 80;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int springVolume = 80;

    @Override
    public @NotNull SaveType saveType() {
        return SaveType.SEPARATE;
    }
}
