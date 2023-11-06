package lol.nezd5553.homing.client.mixin;

import lol.nezd5553.homing.HomingAttack;
import lol.nezd5553.homing.client.mixinaccess.IMinecraftClientMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable>
        implements WindowEventHandler, IMinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    public MinecraftClientMixin(String string) {
        super(string);
    }

    @Shadow
    public Entity getCameraEntity() {
        return null;
    }


    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    public void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.equals(getHighlightedEntity())) cir.setReturnValue(true);
    }

    @Unique
    private Entity highlightedEntity;

    @Unique
    private boolean homingReady;

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (player == null) return;
        if (!player.isSpectator() && !player.hasVehicle()) {
            if (!player.isOnGround()) {
                if (homingReady)
                    setHighlightedEntity(getEntityLooking());
                else
                    setHighlightedEntity(null);
            } else {
                setHighlightedEntity(null);
                setHomingReady();
            }
        } else
            setHighlightedEntity(null);
    }

    @Unique
    private Entity getEntityLooking() {
        // This function is "heavily inspired" by GameRenderer#updateTargetedEntity
        float homingRange = HomingAttack.config.homingRange;

        Entity camera = getCameraEntity();
        Vec3d vec3d2 = camera.getRotationVec(1.0f);
        Vec3d vec3d = camera.getCameraPosVec(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * homingRange, vec3d2.y * homingRange, vec3d2.z * homingRange);
        Box box = camera.getBoundingBox().stretch(vec3d2.multiply(homingRange)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box,
                entity -> !entity.isSpectator() && entity.canHit(), homingRange * homingRange);
        if (entityHitResult != null && entityHitResult.getEntity().isAlive() && player.canSee(entityHitResult.getEntity())) {
            return entityHitResult.getEntity();
        }
        return null;
    }

    @Unique
    public Entity getHighlightedEntity() {
        return highlightedEntity;
    }

    @Unique
    public void setHighlightedEntity(Entity highlightedEntity) {
        this.highlightedEntity = highlightedEntity;
    }

    @Unique
    public void setHomingUnready() {
        homingReady = false;
        setHighlightedEntity(null);
    }

    @Unique
    public void setHomingReady() {
        homingReady = true;
    }

}
