package jackolauncher.enchantment;

import jackolauncher.JackOLauncher;
import jackolauncher.item.ItemJackOLauncher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModEnchantment extends Enchantment {

    protected final int maxLevel;

    protected ModEnchantment(Rarity rarity, String name, int maxLevel) {
        super(rarity, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
        this.setRegistryName(JackOLauncher.MODID, name);
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemJackOLauncher && super.canApply(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ItemJackOLauncher && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}