package me.mfletcher.homing;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.mfletcher.homing.block.HomingBlocks;
import me.mfletcher.homing.client.HomingConfigClient;
import me.mfletcher.homing.client.KeyMappings;
import me.mfletcher.homing.client.ability.BoostAbility;
import me.mfletcher.homing.client.ability.HomingAbility;
import me.mfletcher.homing.client.animation.HomingAnimation;
import me.mfletcher.homing.item.HomingCreativeTabs;
import me.mfletcher.homing.item.HomingItems;
import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.sounds.HomingSounds;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class HomingAttack {
    public static final String MOD_ID = "homing";

    public static HomingConfig config;
    public static HomingConfigClient configClient;

    public static void init() {
        // Write common init code here.
        HomingMessages.register();
        HomingSounds.register();
        HomingBlocks.register();
        HomingItems.register();
        HomingCreativeTabs.register();
        config = ConfigApiJava.registerAndLoadConfig(HomingConfig::new);
        configClient = ConfigApiJava.registerAndLoadConfig(HomingConfigClient::new, RegisterType.CLIENT);

        if (MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.CLIENT) {
            ClientLifecycleEvent.CLIENT_SETUP.register(client -> {
                KeyMappings.register();
                HomingAnimation.register();
            });

            ClientTickEvent.CLIENT_LEVEL_POST.register(minecraft -> {
                HomingAbility.handleHoming();
                BoostAbility.handleBoost();
            });
        }
    }
}
