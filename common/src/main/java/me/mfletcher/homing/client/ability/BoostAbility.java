package me.mfletcher.homing.client.ability;

import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.PlayerHomingData;
import me.mfletcher.homing.client.HomingConfigClient;
import me.mfletcher.homing.client.KeyMappings;
import me.mfletcher.homing.mixin.access.IAbstractClientPlayerMixin;
import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.network.protocol.BoostC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

public class BoostAbility {
    public static void handleBoost() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (shouldStartBoost(player)) {
            HomingMessages.sendToServer(new BoostC2SPacket(true));
            ((IAbstractClientPlayerMixin) player).homing$setBoosting(true);
        } else if (shouldStopBoost(player)) {
            HomingMessages.sendToServer(new BoostC2SPacket(false));
            ((IAbstractClientPlayerMixin) player).homing$setBoosting(false);
        }
    }

    private static boolean shouldStartBoost(@NotNull LocalPlayer player) {
        return !PlayerHomingData.isBoosting(player)
                && (((HomingAttack.configClient.boostActivator.get() == HomingConfigClient.BoostActivator.HOLD && KeyMappings.BOOST_KEY.isDown()) ||
                (HomingAttack.configClient.boostActivator.get() == HomingConfigClient.BoostActivator.TOGGLE && KeyMappings.BOOST_KEY.consumeClick()))
                && HomingAttack.config.enableBoost
                && player.mainSupportingBlockPos.isPresent()
                && (HomingAttack.config.boostHungerDrain <= 0 || player.getFoodData().getFoodLevel() > 6)
                && (HomingAttack.config.boostXpDrain <= 0 || player.experienceProgress > 0 || player.experienceLevel > 0)
                && !player.isUsingItem());
    }

    private static boolean shouldStopBoost(@NotNull LocalPlayer player) {
        return PlayerHomingData.isBoosting(player)
                && (((HomingAttack.configClient.boostActivator.get() == HomingConfigClient.BoostActivator.HOLD && !KeyMappings.BOOST_KEY.isDown()) ||
                (HomingAttack.configClient.boostActivator.get() == HomingConfigClient.BoostActivator.TOGGLE && KeyMappings.BOOST_KEY.consumeClick()))
                || (HomingAttack.config.boostHungerDrain > 0 && player.getFoodData().getFoodLevel() <= 6)
                || (HomingAttack.config.boostXpDrain > 0 && player.experienceProgress <= 0 && player.experienceLevel <= 0)
                || player.isUsingItem());
    }
}
