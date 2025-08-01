package me.mfletcher.homing.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.PlayerHomingAttackInfo;
import me.mfletcher.homing.PlayerHomingData;
import me.mfletcher.homing.mixin.access.IServerPlayerMixin;
import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.network.protocol.BoostS2CPacket;
import me.mfletcher.homing.sounds.HomingSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements IServerPlayerMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public abstract void sendSystemMessage(Component pComponent);

    @Shadow
    public abstract void giveExperiencePoints(int xpPoints);

    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    @Nullable
    private PlayerHomingAttackInfo homing$playerHomingAttackInfo = null;

    @Unique
    @Nullable
    private LivingEntity homing$lastHomingEntity = null;

    @Unique
    private int homing$lastHomingTicks = -1;

    @Unique
    private final MobEffectInstance homing$speedEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MobEffectInstance.INFINITE_DURATION,
            HomingAttack.config.boostLevel, false, false, false);


    public ServerPlayerMixin(Level level, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(level, pos, yaw, gameProfile);
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (homing$playerHomingAttackInfo == null) {
            if (PlayerHomingData.isBoosting(this)) {
                if (HomingAttack.config.boostHungerDrain > 0)
                    causeFoodExhaustion(HomingAttack.config.boostHungerDrain);
                if (HomingAttack.config.boostXpDrain > 0)
                    giveExperiencePoints(-HomingAttack.config.boostXpDrain);
                super.travel(new Vec3(0, 0, 1));
            } else super.travel(movementInput);
        }
    }

    @Inject(method = "setPlayerInput", at = @At("HEAD"), cancellable = true)
    public void onUpdateInput(CallbackInfo ci) {
        if (homing$playerHomingAttackInfo != null)
            ci.cancel();
    }

    @Unique
    public void homing$doHoming(LivingEntity entity) {
        if (entity.distanceTo(this) <= HomingAttack.config.homingRange && homing$playerHomingAttackInfo == null) {
            level().playSound(null, blockPosition(), HomingSounds.HOMING.get(), SoundSource.PLAYERS, HomingAttack.config.homingVolume / 100f, 1.0F);
            homing$playerHomingAttackInfo = new PlayerHomingAttackInfo((ServerPlayer) (Player) this, entity);
            homing$lastHomingEntity = entity;
        } else
            LOGGER.error("Homing attack failed: {}", homing$playerHomingAttackInfo);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source.getEntity() instanceof LivingEntity livingEntity))
            return;
        if (livingEntity.equals(homing$getHomingEntity()))
            cir.setReturnValue(false);
        if (livingEntity.equals(homing$lastHomingEntity) && server.getTickCount() <= homing$lastHomingTicks + HomingAttack.config.homingDamageWaitTicks)
            cir.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (homing$playerHomingAttackInfo != null)
            if (!homing$playerHomingAttackInfo.tick()) {
                homing$playerHomingAttackInfo = null;
                homing$lastHomingTicks = server.getTickCount();
            }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        removeEffect(homing$speedEffect.getEffect());
    }

    @Unique
    public Entity homing$getHomingEntity() {
        if (homing$playerHomingAttackInfo != null)
            return homing$playerHomingAttackInfo.getTarget();
        return null;
    }

    @Unique
    public void homing$setBoosting(boolean boosting) {
        PlayerHomingData.setBoosting(this, boosting);
        if (boosting) {
            addEffect(homing$speedEffect);
            level().playSound(null, blockPosition(), HomingSounds.BOOST.get(), SoundSource.PLAYERS, HomingAttack.config.boostVolume / 100f, 1.0F);
        } else
            removeEffect(homing$speedEffect.getEffect());

        for (Player p : level().players())
            HomingMessages.sendToPlayer(new BoostS2CPacket(getId(), boosting), (ServerPlayer) p);
    }
}
