package me.mfletcher.homing;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import me.mfletcher.homing.block.HomingBlocks;
import me.mfletcher.homing.client.KeyMappings;
import me.mfletcher.homing.client.ability.BoostAbility;
import me.mfletcher.homing.client.ability.HomingAbility;
import me.mfletcher.homing.client.animation.HomingAnimation;
import me.mfletcher.homing.item.HomingCreativeTabs;
import me.mfletcher.homing.item.HomingItems;
import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.network.protocol.ConfigSyncS2CPacket;
import me.mfletcher.homing.sounds.HomingSounds;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class HomingAttack {
    public static final String MOD_ID = "homing";

    public static ModConfig config;

    public static void init() {
        // Write common init code here.
        HomingMessages.register();
        HomingSounds.register();
        HomingBlocks.register();
        HomingItems.register();
        HomingCreativeTabs.register();
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        if(MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.CLIENT) {
            ClientLifecycleEvent.CLIENT_SETUP.register(client -> {
                KeyMappings.register();
                HomingAnimation.register();
            });

            ClientTickEvent.CLIENT_LEVEL_POST.register(minecraft -> {
                HomingAbility.handleHoming();
                BoostAbility.handleBoost();
            });
        }

        if(MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.SERVER) {
            PlayerEvent.PLAYER_JOIN.register(player -> HomingMessages.sendToPlayer(new ConfigSyncS2CPacket(config), player));
        }
    }
}
