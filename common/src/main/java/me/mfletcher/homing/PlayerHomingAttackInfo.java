package me.mfletcher.homing;


import me.mfletcher.homing.network.HomingMessages;
import me.mfletcher.homing.network.protocol.AttackS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;

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
        if (player.getBoundingBox().inflate(HomingAttack.config.homingHitboxAdd).intersects(target.getBoundingBox().inflate(HomingAttack.config.homingHitboxAdd))) {
            target.hurt(player.level().damageSources().playerAttack(player), getDamage());
            Vec3 newVelocity = new Vec3(velocity.x, velocity.y, velocity.z);
            if (HomingAttack.config.homingXZVelocity > 0)
                newVelocity = newVelocity.multiply(-1, 0, -1).normalize().multiply(HomingAttack.config.homingXZVelocity, 0, HomingAttack.config.homingXZVelocity);
            if (HomingAttack.config.homingYVelocity > 0)
                newVelocity = newVelocity.add(0, HomingAttack.config.homingYVelocity / 2f, 0);
            player.setDeltaMovement(newVelocity);
            player.hasImpulse = true;
            player.hurtMarked = true;
            sendHomingPacket(false);
            return false;
        } else if (Objects.requireNonNull(player.getServer()).getTickCount() - startTime >= HomingAttack.config.homingTicksTimeout ||
                (HomingAttack.config.stopHomingOnCollision && player.level().getBlockCollisions(player, player.getBoundingBox()).iterator().hasNext())) {
            sendHomingPacket(false);
            return false;
        } else if (prevDist < (prevDist = player.distanceTo(target))) {
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

    private float getDamage() {
        MutableFloat damage = new MutableFloat(HomingAttack.config.baseHomingDamage);
        player.getArmorSlots().forEach(itemStack -> {
            if (itemStack.getItem() instanceof ArmorItem armorItem)
                damage.add(armorItem.getDefense() * HomingAttack.config.defenseHomingDamageMultiplier + armorItem.getToughness() * HomingAttack.config.toughnessHomingDamageMultiplier);
        });
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
