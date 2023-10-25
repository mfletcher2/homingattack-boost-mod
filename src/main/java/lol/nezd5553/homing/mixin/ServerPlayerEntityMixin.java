package lol.nezd5553.homing.mixin;

import com.mojang.authlib.GameProfile;
import lol.nezd5553.homing.HomingAttack;
import lol.nezd5553.homing.HomingConstants;
import lol.nezd5553.homing.PlayerHomingAttackInfo;
import lol.nezd5553.homing.mixinaccess.IServerPlayerEntityMixin;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IServerPlayerEntityMixin {
    @Shadow
    public abstract void sendMessage(Text message);

    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    @Nullable
    @Getter
    private PlayerHomingAttackInfo playerHomingAttackInfo = null;

    @Unique
    private boolean isBoosting;

    @Unique
    private final StatusEffectInstance speedEffect = new StatusEffectInstance(StatusEffects.SPEED, 20,
            HomingAttack.config.boostLevel, false, false, false);


    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (playerHomingAttackInfo == null) {
            if (isBoosting) {
                addStatusEffect(speedEffect);
                addExhaustion(0.05F);
                super.travel(new Vec3d(0, 0, 1));
            } else super.travel(movementInput);
        } else super.travel(movementInput);
    }

    @Inject(method = "updateInput", at = @At("HEAD"), cancellable = true)
    public void onUpdateInput(CallbackInfo ci) {
        if (playerHomingAttackInfo != null)
            ci.cancel();
    }

    @Unique
    public void doHoming(Entity entity) {
        if (entity.distanceTo(this) <= HomingAttack.config.homingRange && playerHomingAttackInfo == null) {
            playerHomingAttackInfo = new PlayerHomingAttackInfo((ServerPlayerEntity) (PlayerEntity) this, entity);
        } else
            LOGGER.error("Homing attack failed: " + playerHomingAttackInfo);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (playerHomingAttackInfo != null)
            if (!playerHomingAttackInfo.tick())
                playerHomingAttackInfo = null;
    }

    @Unique
    public Entity getHomingEntity() {
        if (playerHomingAttackInfo != null)
            return playerHomingAttackInfo.getTarget();
        return null;
    }

    @Unique
    public void setBoosting(boolean boosting) {
        this.isBoosting = boosting;
        if (!boosting)
            removeStatusEffect(speedEffect.getEffectType());
        PacketByteBuf bufSend = PacketByteBufs.create();
        bufSend.writeInt(getId());
        bufSend.writeBoolean(isBoosting);
        for (PlayerEntity p : getWorld().getPlayers())
            if (p.distanceTo(this) < 128)
                ServerPlayNetworking.send((ServerPlayerEntity) p, HomingConstants.BOOST_PACKET_ID, bufSend);

    }
}
