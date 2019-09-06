package jackolauncher.enchantment;

public class UnwastingEnchantment extends ModEnchantment {

    public UnwastingEnchantment() {
        super(Rarity.UNCOMMON, "unwasting", 3);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 20;
    }
}
