package me.mfletcher.homing.neoforge;

import be.florens.expandability.api.forge.LivingFluidCollisionEvent;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.PlayerHomingData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;


@Mod(HomingAttack.MOD_ID)
public final class HomingAttackNeoForge {
    public HomingAttackNeoForge() {
        // Run our common setup.
        HomingAttack.init();

        NeoForge.EVENT_BUS.addListener(HomingAttackNeoForge::onFluidCollision);
    }

    private static void onFluidCollision(LivingFluidCollisionEvent event) {
        Entity entity = event.getEntity();
        FluidState fluidState = event.getFluidState();
        if (entity instanceof Player player) {
            if (PlayerHomingData.isBoosting(player) && fluidState.is(FluidTags.WATER) && !player.isUsingItem() && !player.isCrouching() && !player.isInWater() && !player.isSwimming()) {
                player.level().addParticle(ParticleTypes.SPLASH, player.getX(), player.getY(), player.getZ(), 0, 3, 0);
                event.setColliding(true);
            } else
                event.setColliding(false);
        } else
            event.setColliding(false);
    }
}
