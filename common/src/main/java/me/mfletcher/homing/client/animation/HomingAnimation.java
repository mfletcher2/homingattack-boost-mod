package me.mfletcher.homing.client.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import me.mfletcher.homing.HomingAttack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HomingAnimation {
    public static final ResourceLocation BOOST_ANIMATION = ResourceLocation.fromNamespaceAndPath(HomingAttack.MOD_ID, "boost");
    public static final ResourceLocation SPINDASH_ANIMATION = ResourceLocation.fromNamespaceAndPath(HomingAttack.MOD_ID, "spindash");
    public static final ResourceLocation DASH_RING_ANIMATION = ResourceLocation.fromNamespaceAndPath(HomingAttack.MOD_ID, "dash_ring_spin");

    public static void register() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ResourceLocation.fromNamespaceAndPath(HomingAttack.MOD_ID, "animation"),
                42,
                (abstractClientPlayer) -> new ModifierLayer<>());
    }

    public static void playAnimation(@NotNull ModifierLayer<IAnimation> modifierLayer, @NotNull ResourceLocation animId) {
        if (PlayerAnimationRegistry.getAnimation(animId) instanceof KeyframeAnimation animation)
            modifierLayer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.CONSTANT), new KeyframeAnimationPlayer(animation)
                    .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL));
    }

    public static void stopAnimations(ModifierLayer<IAnimation> modifierLayer) {
        modifierLayer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.CONSTANT), null);
    }
}
