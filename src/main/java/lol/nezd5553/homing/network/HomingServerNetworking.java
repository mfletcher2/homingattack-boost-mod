package lol.nezd5553.homing.network;

import lol.nezd5553.homing.HomingConstants;
import lol.nezd5553.homing.mixinaccess.IServerPlayerEntityMixin;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class HomingServerNetworking {
    public static void receiveHoming(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        Entity e = player.getWorld().getEntityById(id);
        assert e != null;
        ((IServerPlayerEntityMixin) player).doHoming(e);
    }

    public static void receiveBoost(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ((IServerPlayerEntityMixin) player).setBoosting(buf.readBoolean());
    }

    public static void sendHomingPacket(@NotNull PlayerEntity player, boolean isHoming) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(player.getId());
        buf.writeBoolean(isHoming);
        for (PlayerEntity p : player.getWorld().getPlayers())
            if (p.distanceTo(player) < 128)
                ServerPlayNetworking.send((ServerPlayerEntity) p, HomingConstants.ATTACK_PACKET_ID, buf);
    }

    public static void sendBoostPacket(@NotNull PlayerEntity player, boolean isBoosting) {
        PacketByteBuf bufSend = PacketByteBufs.create();
        bufSend.writeInt(player.getId());
        bufSend.writeBoolean(isBoosting);
        for (PlayerEntity p : player.getWorld().getPlayers())
            if (p.distanceTo(player) < 128)
                ServerPlayNetworking.send((ServerPlayerEntity) p, HomingConstants.BOOST_PACKET_ID, bufSend);
    }
}
