package me.mfletcher.homing.client;

import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.mfletcher.homing.HomingAttack;
import net.minecraft.resources.ResourceLocation;

public class HomingConfigClient extends Config {
    public HomingConfigClient() {
        super(new ResourceLocation(HomingAttack.MOD_ID, "config_client"));
    }

    @ValidatedInt.Restrict(min = 0, max = 100)
    public int reticleVolume = 100;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int dashPanelVolume = 80;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int dashRingVolume = 80;
    @ValidatedInt.Restrict(min = 0, max = 100)
    public int springVolume = 80;
}
