package jackolauncher.item;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeJackOAmmo {// extends IRecipeHidden {
    /*
    public static final Ingredient INGREDIENT_WOOL = Ingredient.fromTag(ItemTags.WOOL);
    public static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);
    public static final Ingredient INGREDIENT_SLIME_BALL = Ingredient.fromItems(Items.SLIME_BALL);
    public static final Ingredient INGREDIENT_BONE_BLOCK = Ingredient.fromItems(Blocks.BONE_BLOCK);
    public static final Ingredient INGREDIENT_FIRE_CHARGE = Ingredient.fromItems(Items.FIRE_CHARGE);
    public static final Ingredient INGREDIENT_ENDER_PEARL = Ingredient.fromItems(Items.ENDER_PEARL);
    public static final Ingredient INGREDIENT_FIREWORK_ROCKET = Ingredient.fromItems(Items.FIREWORK_ROCKET);
    public static final Ingredient INGREDIENT_POTION = Ingredient.fromItems(Items.SPLASH_POTION, Items.LINGERING_POTION);
    public static final Ingredient INGREDIENT_PUMPKIN = Ingredient.fromItems(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON);

    public RecipeJackOAmmo(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public static ItemStack getCraftingResult(ItemStack... inputs) {
        ItemStack resultStack = new ItemStack(JackOLauncher.JACK_O_AMMO, 3);
        CompoundNBT ammoNBT = resultStack.getOrCreateChildTag("AmmoNBT");
        ammoNBT.putBoolean("CanDestroyBlock", true);

        int gunpowderAmount = 0;
        int slimeBallAmount = 0;
        int ironNuggetAmount = 0;

        ItemStack arrowsStack = ItemStack.EMPTY;

        for (ItemStack inputStack : inputs) {
            if (!inputStack.isEmpty()) {
                if (INGREDIENT_PUMPKIN.test(inputStack)) {
                    ammoNBT.put("BlockState", NBTUtil.writeBlockState(Block.getBlockFromItem(inputStack.getItem()).getDefaultState()));
                } else if (INGREDIENT_BONE_BLOCK.test(inputStack)) {
                    ammoNBT.putBoolean("HasBoneMeal", true);
                } else if (INGREDIENT_ENDER_PEARL.test(inputStack)) {
                    ammoNBT.putBoolean("IsEnderPearl", true);
                } else if (INGREDIENT_FIRE_CHARGE.test(inputStack)) {
                    ammoNBT.putBoolean("IsFlaming", true);
                } else if (ItemTags.WOOL.contains(inputStack.getItem())) {
                    ammoNBT.putBoolean("CanDestroyBlocks", false);
                } else if (INGREDIENT_GUNPOWDER.test(inputStack)) {
                    ++gunpowderAmount;
                } else if (INGREDIENT_SLIME_BALL.test(inputStack)) {
                    ++slimeBallAmount;
                } else if (Tags.Items.NUGGETS_IRON.contains(inputStack.getItem())) {
                    ++ironNuggetAmount;
                } else if (INGREDIENT_POTION.test(inputStack)) {
                    ammoNBT.put("PotionNBT", inputStack.write(new CompoundNBT()));
                } else if (INGREDIENT_FIREWORK_ROCKET.test(inputStack)) {
                    if (!inputStack.hasTag()) {
                        CompoundNBT fireworksNBT = new CompoundNBT();
                        fireworksNBT.putByte("Flight", (byte) 2);
                        ammoNBT.put("FireworksNBT", fireworksNBT);
                    } else {
                        // noinspection ConstantConditions
                        ammoNBT.put("FireworksNBT", inputStack.getChildTag("Fireworks"));
                    }
                } else if (inputStack.getItem() instanceof ArrowItem) {
                    if (arrowsStack.isEmpty()) {
                        arrowsStack = inputStack.copy();
                        arrowsStack.setCount(1);
                    } else {
                        arrowsStack.grow(1);
                    }
                }
            }
        }

        ammoNBT.put("ArrowsNBT", arrowsStack.write(new CompoundNBT()));
        ammoNBT.putByte("ExplosionPower", (byte) gunpowderAmount);
        ammoNBT.putByte("BouncesAmount", (byte) slimeBallAmount);
        ammoNBT.putByte("ExtraDamage", (byte) ironNuggetAmount);
        return resultStack;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean matches(IInventory inventory, World world) {
        if (!(inventory instanceof CraftingInventory)) {
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
            } else if (INGREDIENT_WOOL.test(stackInSlot)) {
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
            } else if (stackInSlot.getItem() instanceof ArrowItem) {
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
        ItemStack[] inputs = new ItemStack[inventory.getSizeInventory()];
        for (int slotId = 0; slotId < inventory.getSizeInventory(); ++slotId) {
            inputs[slotId] = inventory.getStackInSlot(slotId);
        }
        return getCraftingResult(inputs);
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
    */
}
