package me.mfletcher.homing.client.ability;

import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.client.KeyMappings;
import me.mfletcher.homing.mixin.access.IMinecraftMixin;
import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.network.protocol.AttackC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class HomingAbility {
    private static boolean homingPressed = false;

    public static void handleHoming() {
        if (Minecraft.getInstance().player == null) return;
        if ((HomingAttack.configClient.homingOnJump && Minecraft.getInstance().options.keyJump.isDown() && !Minecraft.getInstance().player.isCreative())
                || KeyMappings.HOMING_KEY.isDown()) {
            if (homingPressed) return;
            homingPressed = true;
            Entity entity = ((IMinecraftMixin) Minecraft.getInstance()).homing$getHighlightedEntity();
            if (entity != null) {
                HomingMessages.sendToServer(new AttackC2SPacket(((IMinecraftMixin) Minecraft.getInstance()).homing$getHighlightedEntity().getId()));
                ((IMinecraftMixin) Minecraft.getInstance()).homing$setHomingUnready();
            }
        } else
            homingPressed = false;
    }
}
