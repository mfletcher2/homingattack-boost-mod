package lol.nezd5553.homing.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import lol.nezd5553.homing.HomingAttack;
import lol.nezd5553.homing.mixinaccess.IAbstractClientPlayerMixin;
import lol.nezd5553.homing.mixinaccess.IKeyboardInputMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player implements IAbstractClientPlayerMixin {

    @Shadow
    @Final
    public ClientLevel clientLevel;

    @Unique
    private ModifierLayer<IAnimation> homingAnimations;

    @Unique
    private boolean isHoming = false;

    @Unique
    private boolean isBoosting;

    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(level, pos, yaw, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        homingAnimations = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData((AbstractClientPlayer) (Object) this).get(new ResourceLocation("homing", "animation"));
        HomingAttack.LOGGER.info("helo i set up animation layer");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isHoming)
            clientLevel.addParticle(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 0, 0, 0);
    }

    @Unique
    public void startHomingAnimation() {
        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INEXPO),
                new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("homing", "spindash")))
                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(false).setShowRightItem(false)));
        isHoming = true;
    }

    @Unique
    public void startBoostAnimation() {
        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INEXPO),
                new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("homing", "boost")))
                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(false).setShowRightItem(false)));
    }

    @Unique
    public void stopAnimations() {
        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.OUTEXPO), null);
        isHoming = false;
    }

    public boolean isBoosting() {
        return isBoosting;
    }

    public void setBoosting(boolean boosting) {
        if (isBoosting != boosting) {
            if (boosting)
                startBoostAnimation();
            else {
                stopAnimations();
            }
        }

        isBoosting = boosting;


        if (this.equals(Minecraft.getInstance().player)) {
            ((IKeyboardInputMixin) Minecraft.getInstance().player.input).setBoosting(isBoosting);
        }
    }
}
