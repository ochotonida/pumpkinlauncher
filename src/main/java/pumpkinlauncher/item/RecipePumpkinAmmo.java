package pumpkinlauncher.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import pumpkinlauncher.PumpkinLauncher;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipePumpkinAmmo extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private ItemStack resultItem = ItemStack.EMPTY;

    public RecipePumpkinAmmo() {
        setRegistryName("recipepumpkinammo");
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        this.resultItem = ItemStack.EMPTY;

        int pumpkinAmount = 0;
        int gunpowderAmount = 0;
        int fireChargeAmount = 0;
        int woolAmount = 0;
        int slimeBallAmount = 0;
        NBTTagCompound fireworkNBT = null;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (OreDictionary.itemMatches(new ItemStack(Blocks.LIT_PUMPKIN, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    pumpkinAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Items.GUNPOWDER, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    gunpowderAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Items.FIRE_CHARGE, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    fireChargeAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    woolAmount++;
                } else if (new OreIngredient("slimeball").apply(stack)) {
                    slimeBallAmount++;
                } else  if (OreDictionary.itemMatches(new ItemStack(Items.FIREWORKS, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    if (fireworkNBT != null) {
                        return false;
                    } else if (stack.getTagCompound() == null) {
                        fireworkNBT = new NBTTagCompound();
                        fireworkNBT.setByte("Flight", (byte) 2);
                    } else {
                        try {
                            fireworkNBT = (NBTTagCompound) stack.getTagCompound().getTag("Fireworks");
                        } catch (ClassCastException e ) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        if (pumpkinAmount == 1 && (gunpowderAmount != 0 || fireworkNBT != null) && gunpowderAmount <= 4 && fireChargeAmount <= 1 && woolAmount <= 1 && slimeBallAmount <= 127) {
            resultItem = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("power", (byte) gunpowderAmount);
            compound.setByte("bounceAmount", (byte) (slimeBallAmount));
            compound.setBoolean("isFiery", fireChargeAmount > 0);
            compound.setBoolean("canDestroyBlocks", woolAmount < 1);
            if (fireworkNBT != null) {
                compound.setTag("fireworks", fireworkNBT);
            }
            resultItem.setTagCompound(compound);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return resultItem.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return resultItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        NonNullList<ItemStack> list = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            list.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }
        return list;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}
