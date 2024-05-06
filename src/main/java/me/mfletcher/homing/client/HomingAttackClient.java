package me.mfletcher.homing.client;

import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.HomingConstants;
import me.mfletcher.homing.mixinaccess.IAbstractClientPlayerMixin;
import me.mfletcher.homing.mixinaccess.IMinecraftMixin;
import me.mfletcher.homing.networking.HomingMessages;
import me.mfletcher.homing.networking.packet.AttackC2SPacket;
import me.mfletcher.homing.networking.packet.BoostC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

public class HomingAttackClient {
    @Mod.EventBusSubscriber(modid = HomingAttack.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (HomingConstants.HOMING_KEY.consumeClick()) {
                Entity entity = ((IMinecraftMixin) Minecraft.getInstance()).getHighlightedEntity();
                if (entity != null)
                    HomingMessages.sendToServer(new AttackC2SPacket(((IMinecraftMixin) Minecraft.getInstance()).getHighlightedEntity().getId()));
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent event) {
            if (event.type != TickEvent.Type.PLAYER || event.side != LogicalSide.CLIENT || Minecraft.getInstance().player == null)
                return;
            if (HomingConstants.BOOST_KEY.isDown() && !((IAbstractClientPlayerMixin) Minecraft.getInstance().player).isBoosting()
                    && Minecraft.getInstance().player.mainSupportingBlockPos.isPresent() && Minecraft.getInstance().player.getFoodData().getFoodLevel() > 6
                    && !Minecraft.getInstance().player.isUsingItem()) {
                HomingMessages.sendToServer(new BoostC2SPacket(true));
                ((IAbstractClientPlayerMixin) Minecraft.getInstance().player).setBoosting(true);
            } else if (((IAbstractClientPlayerMixin) Minecraft.getInstance().player).isBoosting()
                    && (!HomingConstants.BOOST_KEY.isDown() || Minecraft.getInstance().player.getFoodData().getFoodLevel() <= 6
                    || Minecraft.getInstance().player.isUsingItem())) {
                HomingMessages.sendToServer(new BoostC2SPacket(false));
                ((IAbstractClientPlayerMixin) Minecraft.getInstance().player).setBoosting(false);
            }
        }

        @Mod.EventBusSubscriber(modid = HomingAttack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
        public static class ClientModBusEvents {
            @SubscribeEvent
            public static void onKeyRegister(RegisterKeyMappingsEvent event) {
                event.register(HomingConstants.HOMING_KEY);
                event.register(HomingConstants.BOOST_KEY);
            }
        }
    }
}
