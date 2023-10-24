package lol.nezd5553.homing;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class HomingConstants {
    public static final Identifier ATTACK_PACKET_ID = new Identifier("homing-attack");
    public static final Identifier BOOST_PACKET_ID = new Identifier("homing-boost");
    public static final Identifier HOMING_RANGE_ID = new Identifier("homing-range");

    public static final Set<Item> IRON_ARMOR = new HashSet<>();
    public static final Set<Item> GOLD_ARMOR = new HashSet<>();
    public static final Set<Item> DIAMOND_ARMOR = new HashSet<>();
    public static final Set<Item> NETHERITE_ARMOR = new HashSet<>();

    static {
        IRON_ARMOR.add(Items.IRON_HELMET);
        IRON_ARMOR.add(Items.IRON_CHESTPLATE);
        IRON_ARMOR.add(Items.IRON_LEGGINGS);
        IRON_ARMOR.add(Items.IRON_BOOTS);

        GOLD_ARMOR.add(Items.GOLDEN_HELMET);
        GOLD_ARMOR.add(Items.GOLDEN_CHESTPLATE);
        GOLD_ARMOR.add(Items.GOLDEN_LEGGINGS);
        GOLD_ARMOR.add(Items.GOLDEN_BOOTS);

        DIAMOND_ARMOR.add(Items.DIAMOND_HELMET);
        DIAMOND_ARMOR.add(Items.DIAMOND_CHESTPLATE);
        DIAMOND_ARMOR.add(Items.DIAMOND_LEGGINGS);
        DIAMOND_ARMOR.add(Items.DIAMOND_BOOTS);

        NETHERITE_ARMOR.add(Items.NETHERITE_HELMET);
        NETHERITE_ARMOR.add(Items.NETHERITE_CHESTPLATE);
        NETHERITE_ARMOR.add(Items.NETHERITE_LEGGINGS);
        NETHERITE_ARMOR.add(Items.NETHERITE_BOOTS);
    }
}
