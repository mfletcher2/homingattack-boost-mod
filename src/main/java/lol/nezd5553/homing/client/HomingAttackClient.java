package lol.nezd5553.homing.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import lol.nezd5553.homing.HomingAttack;
import lol.nezd5553.homing.HomingConstants;
import lol.nezd5553.homing.ModConfig;
import lol.nezd5553.homing.client.mixinaccess.IAbstractClientPlayerEntityMixin;
import lol.nezd5553.homing.client.mixinaccess.IMinecraftClientMixin;
import lol.nezd5553.homing.client.network.HomingClientNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class HomingAttackClient implements ClientModInitializer, ModMenuApi {

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
            if (boostBinding.isPressed() && !((IAbstractClientPlayerEntityMixin) client.player).isBoosting()
                    && client.player.supportingBlockPos.isPresent() && client.player.getHungerManager().getFoodLevel() > 6
                    && !client.player.isUsingItem()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(HomingConstants.BOOST_PACKET_ID, buf);
            } else if (((IAbstractClientPlayerEntityMixin) client.player).isBoosting()
                    && (!boostBinding.isPressed() || client.player.getHungerManager().getFoodLevel() <= 6
                    || client.player.isUsingItem())) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                ClientPlayNetworking.send(HomingConstants.BOOST_PACKET_ID, buf);
            }

        });
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.ATTACK_PACKET_ID, HomingClientNetworking::receiveHoming);
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.BOOST_PACKET_ID, HomingClientNetworking::receiveBoost);
        ClientPlayNetworking.registerGlobalReceiver(HomingConstants.HOMING_RANGE_ID, (client, handler, buf, responseSender) -> {
            HomingAttack.config.homingRange = buf.readInt();
        });

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new Identifier("homing", "animation"), 42, (player) -> {
            ModifierLayer<IAnimation> homingAnimation = new ModifierLayer<>();
            return homingAnimation;
        });
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) parent -> AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
}
