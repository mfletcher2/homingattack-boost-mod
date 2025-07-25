package me.mfletcher.homing.mixin.mixins.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.client.HomingConfigClient;
import me.mfletcher.homing.mixin.access.IMinecraftMixin;
import me.mfletcher.homing.sounds.HomingSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler, IMinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    public MinecraftMixin(String string) {
        super(string);
    }

    @Shadow
    public Entity getCameraEntity() {
        return null;
    }

    @Shadow
    public SoundManager getSoundManager() {
        return null;
    }


    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (HomingAttack.configClient.reticleType.get() == HomingConfigClient.ReticleType.GLOWING
                && entity.equals(homing$getHighlightedEntity()))
            cir.setReturnValue(true);
    }

    @Unique
    private LivingEntity homing$highlightedEntity;

    @Unique
    private boolean homing$homingReady;

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (player == null || !HomingAttack.config.enableHoming) return;
        if (!player.isSpectator() && !player.isPassenger()) {
            if (!player.onGround()) {
                if (homing$homingReady) {
                    LivingEntity entityLooking = homing$getEntityLooking();
                    if (entityLooking != null && !entityLooking.equals(homing$getHighlightedEntity()) && HomingAttack.configClient.reticleVolume > 0) {
                        getSoundManager().play(new SimpleSoundInstance(HomingSounds.RETICLE.get(), SoundSource.PLAYERS, HomingAttack.configClient.reticleVolume / 100f, 1, SoundInstance.createUnseededRandom(), player.blockPosition()));
                    }
                    homing$setHighlightedEntity(entityLooking);
                } else
                    homing$setHighlightedEntity(null);
            } else {
                homing$setHighlightedEntity(null);
                homing$setHomingReady();
            }
        } else
            homing$setHighlightedEntity(null);
    }

    @Unique
    private LivingEntity homing$getEntityLooking() {
        if (player == null || level == null) return null;
        float homingRange = HomingAttack.config.homingRange;
        float homingAngleRange = HomingAttack.config.homingAngleRange;

        if (homingAngleRange == 0) {
            // This part is "heavily inspired" by GameRenderer.pick()
            Entity camera = getCameraEntity();
            Vec3 vec32 = camera.getViewVector(1.0f);
            Vec3 vec3 = camera.getEyePosition(1.0f);
            Vec3 vec33 = vec3.add(vec32.x * homingRange, vec32.y * homingRange, vec32.z * homingRange);
            AABB box = camera.getBoundingBox().expandTowards(vec32.scale(homingRange)).inflate(1.0, 1.0, 1.0);
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(camera, vec3, vec33, box,
                    entity -> !entity.isSpectator() && entity.isPickable(), homingRange * homingRange);
            if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity livingEntity
                    && livingEntity.isAlive() && player.hasLineOfSight(livingEntity)) {
                return livingEntity;
            }
        } else {
            float playerAngle = player.yHeadRot;
            LivingEntity closestEntity = null;
            float closestDistance = -1;
            for (Entity entity : level.getEntities(player, AABB.ofSize(player.position(), homingRange * 2, homingRange * 2, homingRange * 2),
                    entity -> entity instanceof LivingEntity)) {
                float distance = entity.distanceTo(player);
                if (distance > homingRange) continue;
                float angle = homing$vec2Angle(entity.position().subtract(player.position()));
                if (Mth.degreesDifferenceAbs(angle - 90, playerAngle) < homingAngleRange
                        && (closestEntity == null || distance < closestDistance) && entity.isAlive() && player.hasLineOfSight(entity)) {
                    closestEntity = (LivingEntity) entity;
                    closestDistance = distance;
                }
            }
            return closestEntity;
        }
        return null;
    }

    @Unique
    public Entity homing$getHighlightedEntity() {
        return homing$highlightedEntity;
    }

    @Unique
    public void homing$setHighlightedEntity(LivingEntity highlightedEntity) {
        this.homing$highlightedEntity = highlightedEntity;
    }

    @Unique
    public void homing$setHomingUnready() {
        homing$homingReady = false;
        homing$setHighlightedEntity(null);
    }

    @Unique
    public void homing$setHomingReady() {
        homing$homingReady = true;
    }

    @Unique
    public boolean homing$isHomingReady() {
        return homing$homingReady;
    }

    @Unique
    private float homing$vec2Angle(Vec3 vec3) {
        return (float) Mth.wrapDegrees(Math.toDegrees(Math.atan2(vec3.z, vec3.x)));
    }
}
