package me.mfletcher.homing.forge;

import be.florens.expandability.api.forge.LivingFluidCollisionEvent;
import dev.architectury.platform.forge.EventBuses;
import me.mfletcher.homing.HomingAttack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HomingAttack.MOD_ID)
public final class HomingAttackForge {
    public HomingAttackForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(HomingAttack.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        HomingAttack.init();

        MinecraftForge.EVENT_BUS.addListener(HomingAttackForge::onFluidCollision);
    }

    private static void onFluidCollision(LivingFluidCollisionEvent event) {
        Entity entity = event.getEntity();
        FluidState fluidState = event.getFluidState();
        event.setResult(HomingAttack.shouldFluidCollision(entity, fluidState) ? Event.Result.ALLOW : Event.Result.DENY);
    }
}
