package lol.nezd5553.homing.client.mixin;

import lol.nezd5553.homing.client.mixinaccess.IKeyboardInputMixin;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input implements IKeyboardInputMixin {

    @Shadow
    @Final
    private GameOptions settings;

    @Unique
    private boolean isBoosting;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        if (isBoosting) {
            this.pressingForward = true;
            this.pressingBack = false;
            this.pressingLeft = false;
            this.pressingRight = false;
            this.movementForward = 1f;
            this.movementSideways = 0f;
            this.jumping = this.settings.jumpKey.isPressed();
            this.sneaking = false;
            ci.cancel();
        }
    }

    @Unique
    public void setBoosting(boolean isBoosting) {
        this.isBoosting = isBoosting;
    }

}
