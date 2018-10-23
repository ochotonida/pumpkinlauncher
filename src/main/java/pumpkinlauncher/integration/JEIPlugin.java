package pumpkinlauncher.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;
import pumpkinlauncher.PumpkinLauncher;

import java.util.Arrays;

@SuppressWarnings("unused")
@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(ItemStackRecipeWrapper.class, (w) -> w, VanillaRecipeCategoryUid.CRAFTING);
        addContainerRecipes(registry);
    }

    @SuppressWarnings("deprecation")
    private void addContainerRecipes(IModRegistry registry) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("power", (byte) 1);
        NBTTagCompound compound0 = compound.copy();
        NBTTagCompound compound1 = compound.copy();
        compound1.setBoolean("isFiery", true);
        NBTTagCompound compound2 = compound.copy();
        compound2.setBoolean("canDestroyBlocks", false);
        NBTTagCompound compound3 = compound.copy();
        compound3.setByte("bounceAmount", (byte) 1);

        NBTTagCompound fireworkCompound = new NBTTagCompound();
        fireworkCompound.setByte("Flight", (byte) 3);
        NBTTagList nbtTagList = new NBTTagList();
        NBTTagCompound fireworkExplosionCompound = new NBTTagCompound();
        int[] aInt = {1};
        fireworkExplosionCompound.setIntArray("Colors", aInt);
        fireworkExplosionCompound.setByte("Type", (byte) 0);
        nbtTagList.appendTag(fireworkExplosionCompound);
        fireworkCompound.setTag("Explosions", nbtTagList);

        NBTTagCompound compound4 = new NBTTagCompound();
        compound4.setTag("fireworks", fireworkCompound.copy());
        NBTTagCompound compound5 = compound.copy();
        compound5.setTag("fireworks", fireworkCompound.copy());
        NBTTagCompound compound6 = new NBTTagCompound();
        compound6.setTag("potionTag", new ItemStack(Items.SPLASH_POTION).writeToNBT(new NBTTagCompound()));
        NBTTagCompound compound7 = new NBTTagCompound();
        compound7.setTag("potionTag", new ItemStack(Items.LINGERING_POTION).writeToNBT(new NBTTagCompound()));
        NBTTagCompound compound8 = new NBTTagCompound();
        compound8.setBoolean("hasBonemeal", true);
        NBTTagCompound compound9 = new NBTTagCompound();
        compound9.setBoolean("isEnderPearl", true);
        NBTTagCompound compound10 = new NBTTagCompound();
        compound10.setByte("extraDamage", (byte) 1);
        NBTTagCompound compound11 = new NBTTagCompound();
        compound11.setTag("arrowTag", new ItemStack(Items.ARROW).writeToNBT(new NBTTagCompound()));
        NBTTagCompound compound12 = new NBTTagCompound();
        compound12.setTag("arrowTag", new ItemStack(Items.TIPPED_ARROW).writeToNBT(new NBTTagCompound()));
        NBTTagCompound compound13 = new NBTTagCompound();
        compound13.setTag("arrowTag", new ItemStack(Items.SPECTRAL_ARROW).writeToNBT(new NBTTagCompound()));

        ItemStack output0 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output0.setTagCompound(compound0);
        ItemStack output1 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output1.setTagCompound(compound1);
        ItemStack output2 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output2.setTagCompound(compound2);
        ItemStack output3 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output3.setTagCompound(compound3);
        ItemStack output4 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output4.setTagCompound(compound4);
        ItemStack output5 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output5.setTagCompound(compound5);
        ItemStack output6 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output6.setTagCompound(compound6);
        ItemStack output7 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output7.setTagCompound(compound7);
        ItemStack output8 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output8.setTagCompound(compound8);
        ItemStack output9 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output9.setTagCompound(compound9);
        ItemStack output10 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output10.setTagCompound(compound10);
        ItemStack output11 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output11.setTagCompound(compound11);
        ItemStack output12 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output12.setTagCompound(compound12);
        ItemStack output13 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO, 3);
        output13.setTagCompound(compound13);

        ItemStack pumpkin = new ItemStack(Blocks.LIT_PUMPKIN, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack gunpowder = new ItemStack(Items.GUNPOWDER, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack arrow = new ItemStack(Items.ARROW, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack firework = new ItemStack(Items.FIREWORKS);
        NBTTagCompound fireworkItemCompound = new NBTTagCompound();
        fireworkItemCompound.setTag("Fireworks", fireworkCompound);
        firework.setTagCompound(fireworkItemCompound);

        registry.addRecipes(Arrays.asList(
                new ItemStackRecipeWrapper(output0, pumpkin, gunpowder),
                new ItemStackRecipeWrapper(output1, pumpkin, gunpowder, new ItemStack(Items.FIRE_CHARGE, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output2, pumpkin, gunpowder, new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output3, pumpkin, gunpowder, new ItemStack(Items.SLIME_BALL, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output4, pumpkin, firework),
                new ItemStackRecipeWrapper(output5, pumpkin, gunpowder, firework),
                new ItemStackRecipeWrapper(output6, pumpkin, new ItemStack(Items.SPLASH_POTION, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output7, pumpkin, new ItemStack(Items.LINGERING_POTION, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output8, pumpkin, new ItemStack(Blocks.BONE_BLOCK, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output9, pumpkin, new ItemStack(Items.ENDER_PEARL, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output10, pumpkin, new ItemStack(Items.IRON_NUGGET, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output11, pumpkin, arrow),
                new ItemStackRecipeWrapper(output12, pumpkin, new ItemStack(Items.TIPPED_ARROW, 1, OreDictionary.WILDCARD_VALUE)),
                new ItemStackRecipeWrapper(output13, pumpkin, new ItemStack(Items.SPECTRAL_ARROW, 1, OreDictionary.WILDCARD_VALUE))
        ));
    }
}
