package pumpkinlauncher.item.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import pumpkinlauncher.PumpkinLauncher;

public class EnchantmentReloadingSpeed extends Enchantment {

    public EnchantmentReloadingSpeed() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
        this.setRegistryName(PumpkinLauncher.MODID + ":reloadingspeed");
        this.setName("reloadingspeed");
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }
}
