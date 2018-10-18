package pumpkinlauncher.common.enchantment;

public class EnchantmentReloadingSpeed extends ModEnchantment {

    public EnchantmentReloadingSpeed() {
        super(Rarity.UNCOMMON, "reloadingspeed", 4);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }
}
