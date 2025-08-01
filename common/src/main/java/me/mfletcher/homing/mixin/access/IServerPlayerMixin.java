package me.mfletcher.homing.mixin.access;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IServerPlayerMixin {
    void homing$doHoming(LivingEntity entity);

    Entity homing$getHomingEntity();

    boolean homing$onTravel(Vec3 movementInput);

    void homing$setBoosting(boolean boosting);
}
