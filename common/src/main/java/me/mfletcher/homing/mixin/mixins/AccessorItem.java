package me.mfletcher.homing.mixin.mixins;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface AccessorItem {
    @Accessor("BASE_ATTACK_DAMAGE_UUID")
    static UUID getBaseAttackDamageUUID() {
        throw new AssertionError();
    }
}
