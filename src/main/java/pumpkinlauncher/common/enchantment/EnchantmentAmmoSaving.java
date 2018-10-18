package pumpkinlauncher.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentArrowInfinite;

public class EnchantmentAmmoSaving extends ModEnchantment {

    public EnchantmentAmmoSaving() {
        super(Rarity.UNCOMMON, "ammosaving", 3);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentArrowInfinite) && super.canApplyTogether(enchantment);
    }
}
