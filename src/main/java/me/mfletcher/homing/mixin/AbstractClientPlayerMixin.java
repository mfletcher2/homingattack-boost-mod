package me.mfletcher.homing.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.mixinaccess.IAbstractClientPlayerMixin;
import me.mfletcher.homing.mixinaccess.IKeyboardInputMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player implements IAbstractClientPlayerMixin {

    @Unique
    private final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    public ClientLevel clientLevel;

    @Unique
    private boolean isHoming = false;

    @Unique
    private boolean isBoosting;

    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(level, pos, yaw, gameProfile);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isHoming)
            clientLevel.addParticle(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 0, 0, 0);
    }

    @Unique
    public void startHomingAnimation() {
//        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INEXPO),
//                new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("homing", "spindash")))
//                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
//                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(false).setShowRightItem(false)));
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) (Object) this).get(new ResourceLocation(HomingAttack.MODID, "animation"));
        if (animation != null) {
            animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("homing", "spindash"))));
            isHoming = true;
        }
    }

    @Unique
    public void startBoostAnimation() {
//        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INEXPO),
//                new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("homing", "boost")))
//                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
//                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(false).setShowRightItem(false)));
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) (Object) this).get(new ResourceLocation(HomingAttack.MODID, "animation"));
        if (animation != null) {
            animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("homing", "boost"))));
        }
    }

    @Unique
    public void stopAnimations() {
//        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.OUTEXPO), null);
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer) (Object) this).get(new ResourceLocation(HomingAttack.MODID, "animation"));
        if (animation != null) {
            animation.setAnimation(null);
        }
        isHoming = false;
    }

    @Unique
    public boolean isBoosting() {
        return isBoosting;
    }

    @Unique
    public void setBoosting(boolean boosting) {
        System.out.println("Setting boosting to " + boosting + " for " + getName().getString());
        if (isBoosting != boosting) {
            System.out.println("Boosting changed for " + getName().getString());
            if (boosting) {
                System.out.println("Starting boost animation");
                startBoostAnimation();
            } else {
                stopAnimations();
            }
        }


        isBoosting = boosting;
        System.out.println("Set boosting to " + isBoosting + " for " + getName().getString());


        if (this.equals(Minecraft.getInstance().player)) {
            ((IKeyboardInputMixin) Minecraft.getInstance().player.input).setBoosting(isBoosting);
        }
    }
}
