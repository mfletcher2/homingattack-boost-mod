package lol.nezd5553.homing.mixinaccess;


import net.minecraft.entity.Entity;

public interface IServerPlayerEntityMixin {
    void doHoming(Entity entity);

    Entity getHomingEntity();

    void setBoosting(boolean boosting);
}
