package me.mfletcher.homing.neoforge;

import be.florens.expandability.api.forge.LivingFluidCollisionEvent;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.PlayerHomingData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

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
            if (PlayerHomingData.isBoosting(player) && !player.isUsingItem() && !player.isCrouching() && !player.isInWater() && !player.isSwimming()) {
                if (fluidState.is(FluidTags.LAVA) && !player.fireImmune() && !EnchantmentHelper.hasFrostWalker(player)) {
                    player.hurt(player.damageSources().hotFloor(), 1);
                }
                player.level().addParticle(ParticleTypes.SPLASH, player.getX(), player.getY(), player.getZ(), 0, 3, 0);
                event.setResult(Event.Result.ALLOW);
            } else
                event.setResult(Event.Result.DENY);
        } else
            event.setResult(Event.Result.DENY);
    }
}
