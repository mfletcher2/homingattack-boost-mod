package lol.nezd5553.homing.client.network;

import lol.nezd5553.homing.client.mixinaccess.IAbstractClientPlayerEntityMixin;
import lol.nezd5553.homing.client.mixinaccess.IMinecraftClientMixin;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class HomingClientNetworking {
    public static void receiveHoming(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!buf.isReadable()) return;
        assert client.world != null;
        PlayerEntity p = (PlayerEntity) client.world.getEntityById(buf.readInt());
        boolean isHoming = buf.readBoolean();
        if (p == null || client.player == null) return;
        if (client.player.equals(p) && !isHoming)
            ((IMinecraftClientMixin) client).setHomingReady();

        if (isHoming) ((IAbstractClientPlayerEntityMixin) p).startHomingAnimation();
        else
            ((IAbstractClientPlayerEntityMixin) p).stopAnimations();

    }

    public static void receiveBoost(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (!buf.isReadable()) return;
        assert client.world != null;
        PlayerEntity p = (PlayerEntity) client.world.getEntityById(buf.readInt());
        if (p == null || client.player == null) return;
        boolean isBoosting = buf.readBoolean();
        ((IAbstractClientPlayerEntityMixin) p).setBoosting(isBoosting);
    }
}
