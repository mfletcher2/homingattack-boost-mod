package me.mfletcher.homing.networking.packet;

import me.mfletcher.homing.mixinaccess.IAbstractClientPlayerMixin;
import me.mfletcher.homing.mixinaccess.IMinecraftMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AttackS2CPacket {
    private final int homingPlayerId;
    private final boolean isHoming;

    public AttackS2CPacket(int homingPlayerId, boolean isHoming) {
        this.homingPlayerId = homingPlayerId;
        this.isHoming = isHoming;
    }

    public AttackS2CPacket(FriendlyByteBuf buf) {
        this.homingPlayerId = buf.readInt();
        this.isHoming = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.homingPlayerId);
        buf.writeBoolean(this.isHoming);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Running on client
            assert Minecraft.getInstance().level != null;
            Player homingPlayer = (Player) Minecraft.getInstance().level.getEntity(this.homingPlayerId);

            if (homingPlayer == null || Minecraft.getInstance().player == null) return;
            if (Minecraft.getInstance().player.equals(homingPlayer) && !isHoming)
                ((IMinecraftMixin) Minecraft.getInstance()).setHomingReady();

            if (isHoming) ((IAbstractClientPlayerMixin) homingPlayer).startHomingAnimation();
            else
                ((IAbstractClientPlayerMixin) homingPlayer).stopAnimations();

        });
        context.setPacketHandled(true);
    }
}
