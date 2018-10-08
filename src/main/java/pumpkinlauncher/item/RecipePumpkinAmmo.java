package pumpkinlauncher.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
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
        int firechargeAmount = 0;
        int woolAmount = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ItemBlock.getItemFromBlock(Blocks.PUMPKIN)) {
                    pumpkinAmount++;
                } else if (stack.getItem() == Items.GUNPOWDER) {
                    gunpowderAmount++;
                } else if (stack.getItem() == Items.FIRE_CHARGE) {
                    firechargeAmount++;
                } else if (stack.getItem() == ItemBlock.getItemFromBlock(Blocks.WOOL)) {
                    woolAmount++;
                } else {
                    return false;
                }
            }
        }

        if (pumpkinAmount == 1 && gunpowderAmount >= 1 && gunpowderAmount <= 3 && firechargeAmount <= 1 && woolAmount <= 1) {
            this.resultItem = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("power", (byte) (gunpowderAmount));
            compound.setBoolean("isFiery", firechargeAmount > 0);
            compound.setBoolean("canDestroyBlocks", woolAmount < 1);
            resultItem.setTagCompound(compound);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return this.resultItem.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.resultItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }
        return nonnulllist;
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
