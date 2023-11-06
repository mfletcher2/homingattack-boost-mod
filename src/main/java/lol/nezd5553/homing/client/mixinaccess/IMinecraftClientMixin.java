package lol.nezd5553.homing.client.mixinaccess;

import net.minecraft.entity.Entity;

public interface IMinecraftClientMixin {
    void setHomingReady();

    void setHomingUnready();

    Entity getHighlightedEntity();

}
