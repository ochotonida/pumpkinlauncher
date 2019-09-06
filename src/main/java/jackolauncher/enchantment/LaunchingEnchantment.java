package jackolauncher.enchantment;

public class LaunchingEnchantment extends ModEnchantment {

    public LaunchingEnchantment() {
        super(Rarity.COMMON, "launching", 5);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }
}
