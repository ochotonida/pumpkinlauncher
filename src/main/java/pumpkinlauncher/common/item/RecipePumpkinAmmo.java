package pumpkinlauncher.common.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArrow;
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
    @SuppressWarnings("ConstantConditions")
    public boolean matches(InventoryCrafting inventory, World world) {
        this.resultItem = ItemStack.EMPTY;

        int pumpkinAmount = 0;
        int gunpowderAmount = 0;
        int fireChargeAmount = 0;
        int woolAmount = 0;
        int slimeBallAmount = 0;
        int bonemealAmount = 0;
        int enderPearlAmount = 0;
        int ironNuggetAmount = 0;
        NBTTagCompound fireworkNBT = null;
        ItemStack potionStack = null;
        ItemStack arrowStack = null;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPumpkin ){
                    pumpkinAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Items.GUNPOWDER, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    gunpowderAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Items.FIRE_CHARGE, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    fireChargeAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    woolAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Blocks.BONE_BLOCK, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    bonemealAmount++;
                } else if (OreDictionary.itemMatches(new ItemStack(Items.ENDER_PEARL, 1, OreDictionary.WILDCARD_VALUE), stack, false)) {
                    enderPearlAmount++;
                } else if (new OreIngredient("nuggetIron").apply(stack)) {
                    ironNuggetAmount++;
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
                        } catch (ClassCastException e) {
                            return false;
                        }
                    }
                } else if (stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION) {
                    if (stack.getTagCompound() == null || potionStack != null) {
                        return false;
                    } else {
                        potionStack = stack;
                    }
                } else if (stack.getItem() instanceof ItemArrow) {
                    if (arrowStack == null) {
                        arrowStack = stack.copy();
                        arrowStack.setCount(1);
                    } else {
                        ItemStack otherStack = stack.copy();
                        otherStack.setCount(arrowStack.getCount());
                        if (ItemStack.areItemStacksEqual(arrowStack, otherStack)) {
                            arrowStack.grow(1);
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        if (pumpkinAmount == 1
                && (gunpowderAmount != 0
                || fireChargeAmount != 0
                || slimeBallAmount != 0
                || bonemealAmount != 0
                || enderPearlAmount != 0
                || ironNuggetAmount != 0
                || fireworkNBT != null
                || potionStack != null
                || arrowStack != null)
                && gunpowderAmount <= 12
                && (woolAmount == 0 || gunpowderAmount >= 1 && woolAmount == 1)
                && fireChargeAmount <= 1
                && slimeBallAmount <= 127
                && bonemealAmount <= 1
                && enderPearlAmount <= 1
                && ironNuggetAmount <= 4) {
            resultItem = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("power", (byte) gunpowderAmount);
            compound.setByte("bounceAmount", (byte) slimeBallAmount);
            compound.setByte("extraDamage", (byte) ironNuggetAmount);
            compound.setBoolean("isFiery", fireChargeAmount > 0);
            compound.setBoolean("canDestroyBlocks", woolAmount < 1);
            compound.setBoolean("hasBonemeal", bonemealAmount > 0);
            compound.setBoolean("isEnderPearl", enderPearlAmount > 0);
            if (fireworkNBT != null) {
                compound.setTag("fireworks", fireworkNBT);
            }
            if (potionStack != null) {
                compound.setTag("potionTag", potionStack.writeToNBT(new NBTTagCompound()));
            }
            if (arrowStack != null) {
                compound.setTag("arrowTag", arrowStack.writeToNBT(new NBTTagCompound()));
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
