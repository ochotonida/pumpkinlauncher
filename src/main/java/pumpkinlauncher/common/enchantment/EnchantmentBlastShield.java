package pumpkinlauncher.common.enchantment;

public class EnchantmentBlastShield extends ModEnchantment {

    public EnchantmentBlastShield() {
        super(Rarity.UNCOMMON, "blastshield", 1);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 20;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }
}
