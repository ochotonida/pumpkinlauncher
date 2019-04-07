package jackolauncher.enchantment;

public class EnchantmentBlastShield extends ModEnchantment {

    public EnchantmentBlastShield() {
        super(Rarity.UNCOMMON, "blast_shield", 1);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 20;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }
}
