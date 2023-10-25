package lol.nezd5553.homing;


import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PlayerHomingAttackInfo {
    private final ServerPlayerEntity player;

    @Getter
    private final Entity target;
    private Vec3d velocity;
    private final int startTime;

    private float prevDist;

    public PlayerHomingAttackInfo(ServerPlayerEntity player, Entity target) {
        this.player = player;
        this.target = target;
        startTime = player.getServer().getTicks();

        velocity = target.getPos().subtract(player.getPos()).normalize().multiply(HomingAttack.config.homingSpeed);
        player.setVelocity(velocity);
        player.velocityDirty = true;
        player.velocityModified = true;
        player.addExhaustion(1f);

        prevDist = player.distanceTo(target);

        sendHomingPacket(true);
    }

    public boolean tick() {
        Box b = player.getBoundingBox().stretch(velocity.multiply(0.5));
        if (b.intersects(target.getBoundingBox())) {
            target.damage(player.getWorld().getDamageSources().playerAttack(player), getDamage());
            player.setVelocity(velocity.multiply(-1, 0, -1).normalize().add(0, 0.5, 0));
            player.velocityModified = true;
            player.velocityDirty = true;
            sendHomingPacket(false);
            return false;
        } else if (player.getServer().getTicks() - startTime >= HomingAttack.config.homingTicksTimeout ||
                player.getWorld().getBlockCollisions(player, player.getBoundingBox()).iterator().hasNext()) {
            sendHomingPacket(false);
            return false;
        } else if (prevDist > (prevDist = player.distanceTo(target))) {
            sendHomingPacket(false);
            return false;
        }

        if (player.getServer().getTicks() % 5 == 0)
            velocity = target.getPos().subtract(player.getPos()).normalize().multiply(HomingAttack.config.homingSpeed);
        player.setVelocity(velocity);
        player.velocityDirty = true;
        player.velocityModified = true;
        return true;
    }

    private float getDamage() {
        final float[] damage = {HomingAttack.config.baseHomingDamage};
        player.getArmorItems().forEach(itemStack -> {
            if (HomingConstants.IRON_ARMOR.contains(itemStack.getItem())) {
                damage[0] += HomingAttack.config.ironArmorHomingDamage;
            } else if (HomingConstants.GOLD_ARMOR.contains(itemStack.getItem())) {
                damage[0] += HomingAttack.config.goldArmorHomingDamage;
            } else if (HomingConstants.DIAMOND_ARMOR.contains(itemStack.getItem())) {
                damage[0] += HomingAttack.config.diamondArmorHomingDamage;
            } else if (HomingConstants.NETHERITE_ARMOR.contains(itemStack.getItem())) {
                damage[0] += HomingAttack.config.netheriteArmorHomingDamage;
            }
        });
        return damage[0];
    }

    private void sendHomingPacket(boolean isHoming) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(player.getId());
        buf.writeBoolean(isHoming);
        for (PlayerEntity p : player.getWorld().getPlayers())
            if (p.distanceTo(player) < 128)
                ServerPlayNetworking.send((ServerPlayerEntity) p, HomingConstants.ATTACK_PACKET_ID, buf);
    }

    public String toString() {
        return player.getDisplayName() + " -> " + target.getDisplayName()
                + " with UUID " + target.getUuidAsString();
    }

}
