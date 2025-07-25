package me.mfletcher.homing;


import me.mfletcher.homing.mixin.mixins.AccessorItem;
import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.network.protocol.AttackS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Map;
import java.util.Objects;

public class PlayerHomingAttackInfo {
    private final ServerPlayer player;

    private final Entity target;
    private Vec3 velocity;
    private final int startTime;

    private float prevDist;

    public PlayerHomingAttackInfo(ServerPlayer player, Entity target) {
        this.player = player;
        this.target = target;
        startTime = Objects.requireNonNull(player.getServer()).getTickCount();

        velocity = target.position().subtract(player.position()).normalize().scale(HomingAttack.config.homingSpeed);
        player.setDeltaMovement(velocity);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.causeFoodExhaustion(1f);

        prevDist = player.distanceTo(target);

        sendHomingPacket(true);
    }

    public boolean tick() {
        if ((player.getBoundingBox().inflate(HomingAttack.config.homingHitboxAdd).intersects(target.getBoundingBox().inflate(HomingAttack.config.homingHitboxAdd)))
                || (prevDist < (prevDist = player.distanceTo(target)) && player.distanceTo(target) <= HomingAttack.config.homingSpeed / 2f)) {
            attackTarget();
            sendHomingPacket(false);
            return false;
        } else if (Objects.requireNonNull(player.getServer()).getTickCount() - startTime >= HomingAttack.config.homingTicksTimeout ||
                (HomingAttack.config.stopHomingOnCollision && player.level().getBlockCollisions(player, player.getBoundingBox()).iterator().hasNext())) {
            sendHomingPacket(false);
            return false;
        }

        if (player.getServer().getTickCount() % 5 == 0)
            velocity = target.position().subtract(player.position()).normalize().scale(HomingAttack.config.homingSpeed);
        player.setDeltaMovement(velocity);
        player.hasImpulse = true;
        player.hurtMarked = true;
        return true;
    }

    private void attackTarget() {
        Vec3 velocityNorm = velocity.normalize();

        target.hurt(player.level().damageSources().playerAttack(player), getDamage());
        if (HomingAttack.config.homingTargetKnockback > 0) {
            target.setDeltaMovement(velocityNorm.x * HomingAttack.config.homingTargetKnockback, HomingAttack.config.homingTargetKnockback / 5, velocityNorm.z * HomingAttack.config.homingTargetKnockback);
            target.hasImpulse = true;
        }

        Vec3 newVelocity = new Vec3(velocity.x, velocity.y, velocity.z);
        if (HomingAttack.config.homingXZKnockbackVelocity > 0)
            newVelocity = velocityNorm.multiply(-HomingAttack.config.homingXZKnockbackVelocity, 0, -HomingAttack.config.homingXZKnockbackVelocity);
        if (HomingAttack.config.homingYKnockbackVelocity > 0)
            newVelocity = newVelocity.add(0, HomingAttack.config.homingYKnockbackVelocity / 2f, 0);
        player.setDeltaMovement(newVelocity);
        player.hasImpulse = true;
        player.hurtMarked = true;
    }

    private float getDamage() {
        MutableFloat damage = new MutableFloat(HomingAttack.config.baseHomingDamage);
        player.getArmorSlots().forEach(itemStack -> {
            if (itemStack.getItem() instanceof ArmorItem armorItem)
                damage.add(armorItem.getDefense() * HomingAttack.config.defenseHomingDamageMultiplier + armorItem.getToughness() * HomingAttack.config.toughnessHomingDamageMultiplier);
        });

        for (Map.Entry<Attribute, AttributeModifier> attributeEntry : player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).entries()) {
            AttributeModifier modifier = attributeEntry.getValue();
            if (modifier.getId() == AccessorItem.getBaseAttackDamageUUID())
                damage.add(modifier.getAmount() * HomingAttack.config.weaponHomingDamageMultiplier);
        }
        return damage.getValue();
    }

    private void sendHomingPacket(boolean isHoming) {
        for (Player p : player.level().players())
            if (p.distanceTo(player) < 128)
                HomingMessages.sendToPlayer(new AttackS2CPacket(player.getId(), isHoming), (ServerPlayer) p);
    }

    public String toString() {
        return player.getDisplayName() + " -> " + target.getDisplayName()
                + " with UUID " + target.getStringUUID();
    }

    public Entity getTarget() {
        return target;
    }
}
