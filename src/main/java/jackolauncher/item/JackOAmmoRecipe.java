package jackolauncher.item;

import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeHidden;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JackOAmmoRecipe extends IRecipeHidden {

    private static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);
    private static final Ingredient INGREDIENT_SLIME_BALL = Ingredient.fromItems(Items.SLIME_BALL);
    private static final Ingredient INGREDIENT_BONE_BLOCK = Ingredient.fromItems(Blocks.BONE_BLOCK);
    private static final Ingredient INGREDIENT_FIRE_CHARGE = Ingredient.fromItems(Items.FIRE_CHARGE);
    private static final Ingredient INGREDIENT_ENDER_PEARL = Ingredient.fromItems(Items.ENDER_PEARL);
    private static final Ingredient INGREDIENT_FIREWORK_ROCKET = Ingredient.fromItems(Items.FIREWORK_ROCKET);
    private static final Ingredient INGREDIENT_POTION = Ingredient.fromItems(Items.SPLASH_POTION, Items.LINGERING_POTION);
    private static final Ingredient INGREDIENT_PUMPKIN = Ingredient.fromItems(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN);

    public JackOAmmoRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean matches(IInventory inventory, World world) {
        if (!(inventory instanceof InventoryCrafting)) {
            return false;
        }

        boolean woolFlag = false;
        boolean potionFlag = false;
        boolean pumpkinFlag = false;
        boolean boneBlockFlag = false;
        boolean enderPearlFlag = false;
        boolean fireChargeFlag = false;
        boolean fireworkRocketFlag = false;

        int gunpowderAmount = 0;
        int slimeBallAmount = 0;
        int ironNuggetAmount = 0;

        ItemStack arrowsStack = ItemStack.EMPTY;

        for (int slotId = 0; slotId < inventory.getSizeInventory(); ++slotId) {
            ItemStack stackInSlot = inventory.getStackInSlot(slotId);

            if (INGREDIENT_PUMPKIN.test(stackInSlot)) {
                if (pumpkinFlag) {
                    return false;
                }
                pumpkinFlag = true;
            } else if (INGREDIENT_POTION.test(stackInSlot)) {
                if (potionFlag || !stackInSlot.hasTag()) {
                    return false;
                }
                potionFlag = true;
            } else if (INGREDIENT_BONE_BLOCK.test(stackInSlot)) {
                if (boneBlockFlag) {
                    return false;
                }
                boneBlockFlag = true;
            } else if (INGREDIENT_FIRE_CHARGE.test(stackInSlot)) {
                if (fireChargeFlag) {
                    return false;
                }
                fireChargeFlag = true;
            } else if (INGREDIENT_ENDER_PEARL.test(stackInSlot)) {
                if (enderPearlFlag) {
                    return false;
                }
                enderPearlFlag = true;
            } else if (INGREDIENT_FIREWORK_ROCKET.test(stackInSlot)) {
                if (fireworkRocketFlag) {
                    return false;
                }
                fireworkRocketFlag = true;
            } else if (ItemTags.WOOL.contains(stackInSlot.getItem())) {
                if (woolFlag) {
                    return false;
                }
                woolFlag = true;
            } else if (INGREDIENT_GUNPOWDER.test(stackInSlot)) {
                if (++gunpowderAmount > 16) {
                    return false;
                }
            } else if (Tags.Items.NUGGETS_IRON.contains(stackInSlot.getItem())) {
                if (++ironNuggetAmount > 4) {
                    return false;
                }
            } else if (stackInSlot.getItem() instanceof ItemArrow) {
                if (arrowsStack.isEmpty() || arrowsStack.getCount() >= 16) {
                    arrowsStack = stackInSlot.copy();
                    arrowsStack.setCount(1);
                } else {
                    ItemStack stackInSlotCopy = stackInSlot.copy();
                    stackInSlotCopy.setCount(arrowsStack.getCount());
                    if (!ItemStack.areItemStacksEqual(arrowsStack, stackInSlotCopy)) {
                        return false;
                    }
                    arrowsStack.grow(1);
                }
            } else if (INGREDIENT_SLIME_BALL.test(stackInSlot)) {
                ++slimeBallAmount;
            } else if (!stackInSlot.isEmpty()) {
                return false;
            }
        }

        return pumpkinFlag && (potionFlag || boneBlockFlag || enderPearlFlag || fireChargeFlag || fireworkRocketFlag || gunpowderAmount > 0 || ironNuggetAmount > 0 || slimeBallAmount > 0 || !arrowsStack.isEmpty()) && (!woolFlag || gunpowderAmount >= 1 && woolFlag);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        ItemStack resultStack = new ItemStack(JackOLauncher.JACK_O_AMMO);
        NBTTagCompound ammoNBT = resultStack.getOrCreateChildTag("AmmoNBT");
        ammoNBT.setBoolean("CanDestroyBlock", true);

        int gunpowderAmount = 0;
        int slimeBallAmount = 0;
        int ironNuggetAmount = 0;

        ItemStack arrowsStack = ItemStack.EMPTY;

        for (int slotId = 0; slotId < inventory.getSizeInventory(); ++slotId) {
            ItemStack stackInSlot = inventory.getStackInSlot(slotId);
            if (!stackInSlot.isEmpty()) {
                if (INGREDIENT_BONE_BLOCK.test(stackInSlot)) {
                    ammoNBT.setBoolean("HasBoneMeal", true);
                } else if (INGREDIENT_ENDER_PEARL.test(stackInSlot)) {
                    ammoNBT.setBoolean("IsEnderPearl", true);
                } else if (INGREDIENT_FIRE_CHARGE.test(stackInSlot)) {
                    ammoNBT.setBoolean("IsFlaming", true);
                } else if (ItemTags.WOOL.contains(stackInSlot.getItem())) {
                    ammoNBT.setBoolean("CanDestroyBlocks", false);
                } else if (INGREDIENT_GUNPOWDER.test(stackInSlot)) {
                    ++gunpowderAmount;
                } else if (INGREDIENT_SLIME_BALL.test(stackInSlot)) {
                    ++slimeBallAmount;
                } else if (Tags.Items.NUGGETS_IRON.contains(stackInSlot.getItem())) {
                    ++ironNuggetAmount;
                } else if (INGREDIENT_POTION.test(stackInSlot)) {
                    ammoNBT.setTag("PotionNBT", stackInSlot.write(new NBTTagCompound()));
                } else if (INGREDIENT_FIREWORK_ROCKET.test(stackInSlot)) {
                    if (!stackInSlot.hasTag()) {
                        NBTTagCompound fireworksNBT = new NBTTagCompound();
                        fireworksNBT.setByte("Flight", (byte) 2);
                        ammoNBT.setTag("FireworksNBT", fireworksNBT);
                    } else {
                        // noinspection ConstantConditions
                        ammoNBT.setTag("FireworksNBT", stackInSlot.getChildTag("Fireworks"));
                    }
                } else if (stackInSlot.getItem() instanceof ItemArrow) {
                    if (arrowsStack.isEmpty()) {
                        arrowsStack = stackInSlot.copy();
                        arrowsStack.setCount(1);
                    } else {
                        arrowsStack.grow(1);
                    }
                }
            }
        }

        ammoNBT.setTag("ArrowsNBT", arrowsStack.write(new NBTTagCompound()));
        ammoNBT.setByte("ExplosionPower", (byte) gunpowderAmount);
        ammoNBT.setByte("BouncesAmount", (byte) slimeBallAmount);
        ammoNBT.setByte("ExtraDamage", (byte) ironNuggetAmount);
        return resultStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(JackOLauncher.JACK_O_AMMO);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return JackOLauncher.CRAFTING_SPECIAL_JACK_O_AMMO;
    }
}
