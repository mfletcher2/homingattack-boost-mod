package lol.nezd5553.homing.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import lol.nezd5553.homing.HomingAttack;
import lol.nezd5553.homing.HomingConstants;
import lol.nezd5553.homing.mixinaccess.IAbstractClientPlayerMixin;
import lol.nezd5553.homing.mixinaccess.IMinecraftMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class HomingAttackClient implements ClientModInitializer {
    private static void receiveHoming(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (!buf.isReadable()) return;
        assert client.level != null;
        Player p = (Player) client.level.getEntity(buf.readInt());
        boolean isHoming = buf.readBoolean();
        if (p == null || client.player == null) return;
        if (client.player.equals(p) && !isHoming)
            ((IMinecraftMixin) client).setHomingReady();

        if (isHoming) ((IAbstractClientPlayerMixin) p).startHomingAnimation();
        else
            ((IAbstractClientPlayerMixin) p).stopAnimations();

    }

    private static void receiveBoost(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (!buf.isReadable()) return;
        assert client.level != null;
        Player p = (Player) client.level.getEntity(buf.readInt());
        if (p == null || client.player == null) return;
        boolean isBoosting = buf.readBoolean();
        ((IAbstractClientPlayerMixin) p).setBoosting(isBoosting);
    }

    @Override
    public void onInitializeClient() {
        KeyMapping homingBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.homing.attack",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.homing.main"));

        KeyMapping boostBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.homing.boost",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.homing.main"));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (homingBinding.consumeClick()) {
                if (((IMinecraftMixin) client).getHighlightedEntity() != null) {
                    int id = ((IMinecraftMixin) client).getHighlightedEntity().getId();
                    FriendlyByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(id);
                    ClientPlayNetworking.send(HomingConstants.ATTACK_PACKET_ID, buf);
                    ((IMinecraftMixin) client).setHomingUnready();
                }
            }
            if (boostBinding.isDown() && !((IAbstractClientPlayerMixin) client.player).isBoosting()
                    && client.player.mainSupportingBlockPos.isPresent() && client.player.getFoodData().getFoodLevel() > 6
                    && !client.player.isUsingItem()) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(HomingConstants.BOOST_PACKET_ID, buf);
            } else if (((IAbstractClientPlayerMixin) client.player).isBoosting()
                    && (!boostBinding.isDown() || client.player.getFoodData().getFoodLevel() <= 6
                    || client.player.isUsingItem())) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                ClientPlayNetworking.send(HomingConstants.BOOST_PACKET_ID, buf);
            }

        });
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.ATTACK_PACKET_ID, HomingAttackClient::receiveHoming);
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.BOOST_PACKET_ID, HomingAttackClient::receiveBoost);
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.HOMING_RANGE_ID, (client, handler, buf, responseSender) -> {
            HomingAttack.config.homingRange = buf.readInt();
        });

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new ResourceLocation("homing", "animation"), 42, (player) -> {
            ModifierLayer<IAnimation> homingAnimation = new ModifierLayer<>();
            return homingAnimation;
        });
    }
}
