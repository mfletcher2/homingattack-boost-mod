package lol.nezd5553.homing.client;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import lol.nezd5553.homing.HomingAttack;
import lol.nezd5553.homing.HomingConstants;
import lol.nezd5553.homing.mixinaccess.IAbstractClientPlayerEntityMixin;
import lol.nezd5553.homing.mixinaccess.IMinecraftClientMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class HomingAttackClient implements ClientModInitializer {
    private static void receiveHoming(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(!buf.isReadable()) return;
        assert client.world != null;
        PlayerEntity p = (PlayerEntity) client.world.getEntityById(buf.readInt());
        boolean isHoming = buf.readBoolean();
        if(p == null || client.player == null) return;
        if (client.player.equals(p) && !isHoming)
            ((IMinecraftClientMixin) client).setHomingReady();

        if (isHoming) ((IAbstractClientPlayerEntityMixin) p).startHomingAnimation();
        else
            ((IAbstractClientPlayerEntityMixin) p).stopAnimations();

    }
    private static void receiveBoost(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(!buf.isReadable()) return;
        assert client.world != null;
        PlayerEntity p = (PlayerEntity) client.world.getEntityById(buf.readInt());
        if(p == null || client.player == null) return;
        boolean isBoosting = buf.readBoolean();
        ((IAbstractClientPlayerEntityMixin) p).setBoosting(isBoosting);
    }

    @Override
    public void onInitializeClient() {
        KeyBinding homingBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.homing.attack",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.homing.main"));

        KeyBinding boostBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.homing.boost",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.homing.main"));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (homingBinding.wasPressed()) {
                if (((IMinecraftClientMixin) client).getHighlightedEntity() != null) {
                    int id = ((IMinecraftClientMixin) client).getHighlightedEntity().getId();
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(id);
                    ClientPlayNetworking.send(HomingConstants.ATTACK_PACKET_ID, buf);
                    ((IMinecraftClientMixin) client).setHomingUnready();
                }
            }
            if (boostBinding.isPressed() && !((IAbstractClientPlayerEntityMixin) client.player).isBoosting() && client.player.supportingBlockPos.isPresent()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(HomingConstants.BOOST_PACKET_ID, buf);
                //((IAbstractClientPlayerEntityMixin)client.player).setBoosting(true);
            } else if (((IAbstractClientPlayerEntityMixin) client.player).isBoosting() && !boostBinding.isPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                ClientPlayNetworking.send(HomingConstants.BOOST_PACKET_ID, buf);
//                ((IAbstractClientPlayerEntityMixin)client.player).setBoosting(false);

            }

        });
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.ATTACK_PACKET_ID, HomingAttackClient::receiveHoming);
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.BOOST_PACKET_ID, HomingAttackClient::receiveBoost);
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.HOMING_RANGE_ID, (client, handler, buf, responseSender) -> {
            HomingAttack.config.homingRange = buf.readInt();
        });

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new Identifier("homing", "animation"), 42, (player) -> {
            ModifierLayer<IAnimation> homingAnimation = new ModifierLayer<>();
            return homingAnimation;
        });
    }
}
