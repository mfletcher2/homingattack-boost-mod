package lol.nezd5553.homing;

import lol.nezd5553.homing.network.HomingServerNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomingAttack implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("homing-attack");

    public static ModConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("Homing Attack initialized!");

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        ServerPlayNetworking.registerGlobalReceiver(HomingConstants.ATTACK_PACKET_ID, HomingServerNetworking::receiveHoming);
        ServerPlayNetworking.registerGlobalReceiver(HomingConstants.BOOST_PACKET_ID, HomingServerNetworking::receiveBoost);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(config.homingRange);
                ServerPlayNetworking.send(handler.getPlayer(), HomingConstants.HOMING_RANGE_ID, buf);
            });
        }
    }

}
