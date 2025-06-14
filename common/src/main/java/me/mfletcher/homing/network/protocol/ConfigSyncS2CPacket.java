package me.mfletcher.homing.network.protocol;

import dev.architectury.networking.NetworkManager;
import me.mfletcher.homing.HomingAttack;
import me.mfletcher.homing.ModConfig;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class ConfigSyncS2CPacket {
    private final int homingRange;
    private final float boostHungerDrain;
    private final int boostXpDrain;

    public ConfigSyncS2CPacket(ModConfig config) {
        this.homingRange = config.homingRange;
        this.boostHungerDrain = config.boostHungerDrain;
        this.boostXpDrain = config.boostXpDrain;
    }

    public ConfigSyncS2CPacket(FriendlyByteBuf buf) {
        homingRange = buf.readInt();
        boostHungerDrain = buf.readFloat();
        boostXpDrain = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(homingRange);
        buf.writeFloat(boostHungerDrain);
        buf.writeInt(boostXpDrain);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();
        context.queue(() -> {
            // Running on client
            HomingAttack.config.homingRange = this.homingRange;
            HomingAttack.config.boostHungerDrain = this.boostHungerDrain;
            HomingAttack.config.boostXpDrain = this.boostXpDrain;
        });
    }
}
