package pumpkinlauncher.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import pumpkinlauncher.PumpkinLauncher;

import javax.annotation.Nonnull;
import java.util.Arrays;

@mezz.jei.api.JEIPlugin
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        registry.handleRecipes(PumpkinAmmoRecipeWrapper.class, (w) -> w, VanillaRecipeCategoryUid.CRAFTING);
        addContainerRecipes(registry);
    }

    @SuppressWarnings("deprecation")
    private void addContainerRecipes(@Nonnull IModRegistry registry) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("power", (byte) 1);
        NBTTagCompound compound0 = compound.copy();
        compound0.setByte("power", (byte) 3);
        NBTTagCompound compound1 = compound.copy();
        compound1.setBoolean("isFiery", true);
        NBTTagCompound compound2 = compound.copy();
        compound2.setBoolean("canDestroyBlocks", false);
        NBTTagCompound compound3 = compound.copy();
        compound3.setByte("bounceAmount", (byte) 1);

        ItemStack output0 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output0.setTagCompound(compound0);
        ItemStack output1 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output1.setTagCompound(compound1);
        ItemStack output2 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output2.setTagCompound(compound2);
        ItemStack output3 = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        output3.setTagCompound(compound3);

        ItemStack pumpkin = new ItemStack(Blocks.LIT_PUMPKIN, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack gunpowder = new ItemStack(Items.GUNPOWDER, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack firecharge = new ItemStack(Items.FIRE_CHARGE, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack woolblock = new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack slimeBall = new ItemStack(Items.SLIME_BALL, 1, OreDictionary.WILDCARD_VALUE);

        registry.addRecipes(Arrays.asList(
                new PumpkinAmmoRecipeWrapper(output0, pumpkin, gunpowder, gunpowder, gunpowder),
                new PumpkinAmmoRecipeWrapper(output1, pumpkin, gunpowder, firecharge),
                new PumpkinAmmoRecipeWrapper(output2, pumpkin, gunpowder, woolblock),
                new PumpkinAmmoRecipeWrapper(output3, pumpkin, gunpowder, slimeBall)
        ));
    }
}
