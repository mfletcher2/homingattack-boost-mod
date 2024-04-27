package me.mfletcher.homing;

import me.mfletcher.homing.mixinaccess.IServerPlayerMixin;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomingAttack implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("homing-attack");

    public static ModConfig config;

    private static void receiveHoming(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        Entity e = player.level().getEntity(id);
        assert e != null;
        ((IServerPlayerMixin) player).doHoming(e);
    }

    private static void receiveBoost(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ((IServerPlayerMixin) player).setBoosting(buf.readBoolean());
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Homing Attack initialized!");

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        ServerPlayNetworking.registerGlobalReceiver(HomingConstants.ATTACK_PACKET_ID, HomingAttack::receiveHoming);
        ServerPlayNetworking.registerGlobalReceiver(HomingConstants.BOOST_PACKET_ID, HomingAttack::receiveBoost);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeInt(config.homingRange);
                ServerPlayNetworking.send(handler.getPlayer(), HomingConstants.HOMING_RANGE_ID, buf);
            });
        }
    }

}
