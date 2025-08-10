package me.mfletcher.homing.mixin.mixins.client;

import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.PlayerHomingData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
@Environment(EnvType.CLIENT)
public abstract class PlayerMixinClient extends LivingEntity {
    protected PlayerMixinClient(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void onAiStep(CallbackInfo ci) {
        if (((LivingEntity) this) instanceof AbstractClientPlayer acp)
            if (PlayerHomingData.isHoming(acp)) {
                acp.clientLevel.addParticle((SimpleParticleType) HomingAttack.configClient.homingParticle.get(), getX(), getY(), getZ(), 0, 0, 0);
            }
    }
}
