package lol.nezd5553.homing.client.mixin;

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
import lol.nezd5553.homing.client.mixinaccess.IAbstractClientPlayerEntityMixin;
import lol.nezd5553.homing.client.mixinaccess.IKeyboardInputMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements IAbstractClientPlayerEntityMixin {

    @Shadow
    @Final
    public ClientWorld clientWorld;

    @Unique
    private ModifierLayer<IAnimation> homingAnimations;

    @Unique
    private boolean isHoming = false;

    @Unique
    private boolean isBoosting;

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/world/ClientWorld;Lcom/mojang/authlib/GameProfile;)V", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        homingAnimations = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData((AbstractClientPlayerEntity) (Object) this).get(new Identifier("homing", "animation"));
        HomingAttack.LOGGER.info("helo i set up animation layer");
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (isHoming)
            clientWorld.addParticle(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 0, 0, 0);
    }

    @Unique
    public void startHomingAnimation() {
        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INEXPO),
                new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier("homing", "spindash")))
                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(false).setShowLeftItem(false).setShowRightItem(false)));
        isHoming = true;
    }

    @Unique
    public void startBoostAnimation() {
        homingAnimations.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INEXPO),
                new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier("homing", "boost")))
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


        if (this.equals(MinecraftClient.getInstance().player)) {
            ((IKeyboardInputMixin) MinecraftClient.getInstance().player.input).setBoosting(isBoosting);
        }
    }
}
