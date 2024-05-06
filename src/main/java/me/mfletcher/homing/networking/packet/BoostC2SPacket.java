package me.mfletcher.homing.networking.packet;

import me.mfletcher.homing.mixinaccess.IServerPlayerMixin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoostC2SPacket {
    private final boolean isBoosting;

    public BoostC2SPacket(boolean isBoosting) {
        this.isBoosting = isBoosting;
    }

    public BoostC2SPacket(FriendlyByteBuf buf) {
        this.isBoosting = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isBoosting);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Running on server
            ((IServerPlayerMixin) context.getSender()).setBoosting(isBoosting);
        });
        context.setPacketHandled(true);
    }
}
