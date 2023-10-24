package lol.nezd5553.homing.mixin;

import lol.nezd5553.homing.mixinaccess.IServerPlayerEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements Targeter {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    public void onTryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof ServerPlayerEntity
                && ((PlayerEntity) target).getArmor() != 0
                && this.equals(((IServerPlayerEntityMixin) target).getHomingEntity()))
            cir.setReturnValue(false);
    }
}
