package me.mfletcher.homing.networking.packet;

import me.mfletcher.homing.mixinaccess.IAbstractClientPlayerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoostS2CPacket {
    private final int boostPlayerId;
    private final boolean isBoosting;

    public BoostS2CPacket(int boostPlayerId, boolean isBoosting) {
        this.boostPlayerId = boostPlayerId;
        this.isBoosting = isBoosting;
    }

    public BoostS2CPacket(FriendlyByteBuf buf) {
        this.boostPlayerId = buf.readInt();
        this.isBoosting = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.boostPlayerId);
        buf.writeBoolean(this.isBoosting);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Running on client
            Player boostPlayer = (Player) Minecraft.getInstance().level.getEntity(this.boostPlayerId);
            System.out.println("player is " + boostPlayer);
            if (boostPlayer == null || Minecraft.getInstance().player == null) return;
            System.out.println("player is not null");
            ((IAbstractClientPlayerMixin) boostPlayer).setBoosting(isBoosting);
        });
        context.setPacketHandled(true);
    }
}
