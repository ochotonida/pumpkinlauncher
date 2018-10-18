package pumpkinlauncher.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.common.item.ItemPumpkinLauncher;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModEnchantment extends Enchantment {

    protected final int maxLevel;

    protected ModEnchantment(Rarity rarity, String name, int maxLevel) {
        super(rarity, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
        this.setRegistryName(PumpkinLauncher.MODID + ":" + name);
        this.setName(name);
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemPumpkinLauncher && super.canApply(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ItemPumpkinLauncher && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}
