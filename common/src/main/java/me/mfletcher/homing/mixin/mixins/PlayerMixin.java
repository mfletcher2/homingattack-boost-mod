package me.mfletcher.homing.mixin.mixins;

import me.mfletcher.homing.mixin.access.IServerPlayerMixin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void onTravel(Vec3 movementInput, CallbackInfo ci) {
        if (((LivingEntity) this) instanceof ServerPlayer serverPlayer) {
            if (((IServerPlayerMixin) serverPlayer).homing$onTravel(movementInput))
                ci.cancel();
        }
    }
}
