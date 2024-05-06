package me.mfletcher.homing.networking.packet;

import me.mfletcher.homing.mixinaccess.IServerPlayerMixin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AttackC2SPacket {
    int targetId;

    public AttackC2SPacket(int targetId) {
        this.targetId = targetId;
    }

    public AttackC2SPacket(FriendlyByteBuf buf) {
        this.targetId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.targetId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Running on server
            System.out.println("Received homing packet");
            Level level = context.getSender().level();
            ((IServerPlayerMixin) context.getSender()).doHoming(level.getEntity(this.targetId));
        });
        context.setPacketHandled(true);
    }
}
