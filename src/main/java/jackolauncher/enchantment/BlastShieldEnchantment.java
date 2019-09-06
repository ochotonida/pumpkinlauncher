package jackolauncher.enchantment;

public class BlastShieldEnchantment extends ModEnchantment {

    public BlastShieldEnchantment() {
        super(Rarity.UNCOMMON, "blast_shield", 1);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 20;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }
}
