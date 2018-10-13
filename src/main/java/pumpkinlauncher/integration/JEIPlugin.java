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

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(PumpkinAmmoRecipeWrapper.class, (w) -> w, VanillaRecipeCategoryUid.CRAFTING);
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

        ItemStack output0 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output0.setTagCompound(compound0);
        ItemStack output1 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output1.setTagCompound(compound1);
        ItemStack output2 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output2.setTagCompound(compound2);
        ItemStack output3 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output3.setTagCompound(compound3);
        ItemStack output4 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output4.setTagCompound(compound4);
        ItemStack output5 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output5.setTagCompound(compound5);

        ItemStack pumpkin = new ItemStack(Blocks.LIT_PUMPKIN, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack gunpowder = new ItemStack(Items.GUNPOWDER, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack fireCharge = new ItemStack(Items.FIRE_CHARGE, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack woolBlock = new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack slimeBall = new ItemStack(Items.SLIME_BALL, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack firework = new ItemStack(Items.FIREWORKS);
        NBTTagCompound fireworkItemCompound = new NBTTagCompound();
        fireworkItemCompound.setTag("Fireworks", fireworkCompound);
        firework.setTagCompound(fireworkItemCompound);

        registry.addRecipes(Arrays.asList(
                new PumpkinAmmoRecipeWrapper(output0, pumpkin, gunpowder),
                new PumpkinAmmoRecipeWrapper(output1, pumpkin, gunpowder, fireCharge),
                new PumpkinAmmoRecipeWrapper(output2, pumpkin, gunpowder, woolBlock),
                new PumpkinAmmoRecipeWrapper(output3, pumpkin, gunpowder, slimeBall),
                new PumpkinAmmoRecipeWrapper(output4, pumpkin, firework),
                new PumpkinAmmoRecipeWrapper(output5, pumpkin, gunpowder, firework)
        ));
    }
}
