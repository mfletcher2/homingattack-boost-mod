package me.mfletcher.homing.networking;

import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.networking.packet.AttackC2SPacket;
import me.mfletcher.homing.networking.packet.AttackS2CPacket;
import me.mfletcher.homing.networking.packet.BoostC2SPacket;
import me.mfletcher.homing.networking.packet.BoostS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class HomingMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(HomingAttack.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(AttackC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AttackC2SPacket::new)
                .encoder(AttackC2SPacket::toBytes)
                .consumerMainThread(AttackC2SPacket::handle)
                .add();
        net.messageBuilder(AttackS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(AttackS2CPacket::new)
                .encoder(AttackS2CPacket::toBytes)
                .consumerMainThread(AttackS2CPacket::handle)
                .add();
        net.messageBuilder(BoostC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(BoostC2SPacket::new)
                .encoder(BoostC2SPacket::toBytes)
                .consumerMainThread(BoostC2SPacket::handle)
                .add();
        net.messageBuilder(BoostS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BoostS2CPacket::new)
                .encoder(BoostS2CPacket::toBytes)
                .consumerMainThread(BoostS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}


