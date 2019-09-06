package jackolauncher.enchantment;

public class ReloadingEnchantment extends ModEnchantment {

    public ReloadingEnchantment() {
        super(Rarity.UNCOMMON, "reloading", 4);
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
