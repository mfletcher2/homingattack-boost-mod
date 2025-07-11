package me.mfletcher.homing.client;

import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedRegistryType;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.mfletcher.homing.HomingAttack;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HomingConfigClient extends Config {
    public HomingConfigClient() {
        super(new ResourceLocation(HomingAttack.MOD_ID, "config_client"));
    }

    public boolean showHomingAnimation = true;
    public boolean showBoostAnimation = true;
    public boolean showDashRingAnimation = true;

    @ValidatedInt.Restrict(min = 0, max = 100)
    public int reticleVolume = 100;

    public ValidatedEnum<ReticleType> reticleType = new ValidatedEnum<>(ReticleType.GENERATIONS, ValidatedEnum.WidgetType.CYCLING);

    public ValidatedField<ParticleType<?>> homingParticle = ValidatedRegistryType.of(ParticleTypes.ELECTRIC_SPARK, BuiltInRegistries.PARTICLE_TYPE,
            particleTypeHolder -> particleTypeHolder.value() instanceof SimpleParticleType);

    public enum ReticleType implements EnumTranslatable {
        GENERATIONS, GLOWING, NONE;

        @Override
        public @NotNull String prefix() {
            return HomingAttack.MOD_ID + ".reticle_type";
        }
    }
}
