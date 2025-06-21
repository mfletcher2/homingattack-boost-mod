package me.mfletcher.homing.mixin.access;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface IServerPlayerMixin {
    void homing$doHoming(LivingEntity entity);

    Entity homing$getHomingEntity();

    void homing$setBoosting(boolean boosting);
}
