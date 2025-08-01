package me.mfletcher.homing.network.protocol;

import dev.architectury.networking.NetworkManager;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.mixin.access.IServerPlayerMixin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class AttackC2SPacket {
    int targetId;

    public AttackC2SPacket(int targetId) {
        this.targetId = targetId;
    }

    public AttackC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.targetId);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            // Running on server
            if (!HomingAttack.config.enableHoming) return;

            Level level = context.getPlayer().level();
            if (level.getEntity(this.targetId) instanceof LivingEntity livingEntity)
                ((IServerPlayerMixin) context.getPlayer()).homing$doHoming(livingEntity);
        });
    }
}
