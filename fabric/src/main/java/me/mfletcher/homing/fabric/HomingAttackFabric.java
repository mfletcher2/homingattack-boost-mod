package me.mfletcher.homing.fabric;

import be.florens.expandability.api.fabric.LivingFluidCollisionCallback;
import me.mfletcher.homing.HomingAttack;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;

public final class HomingAttackFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HomingAttack.init();

        LivingFluidCollisionCallback.EVENT.register(HomingAttackFabric::onFluidCollision);
    }

    private static boolean onFluidCollision(LivingEntity entity, FluidState fluidState) {
        return HomingAttack.shouldFluidCollision(entity, fluidState);
    }
}
