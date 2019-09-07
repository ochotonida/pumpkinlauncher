package jackolauncher.compat;


import jackolauncher.JackOLauncher;
import jackolauncher.item.JackOAmmoRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@JeiPlugin
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JackOLauncher.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Set<IRecipe> recipes = new HashSet<>();
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(JackOAmmoRecipe.INGREDIENT_GUNPOWDER), Items.GUNPOWDER));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(JackOAmmoRecipe.INGREDIENT_FIRE_CHARGE), Items.FIRE_CHARGE));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(JackOAmmoRecipe.INGREDIENT_SLIME_BALL), Items.SLIME_BALL));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(Ingredient.fromItems(Items.IRON_NUGGET)), Items.IRON_NUGGET));
        recipes.add(new RecipeJackOAmmoDummy(Arrays.asList(JackOAmmoRecipe.INGREDIENT_GUNPOWDER, JackOAmmoRecipe.INGREDIENT_WOOL), Items.GUNPOWDER, Blocks.WHITE_WOOL.asItem()));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(JackOAmmoRecipe.INGREDIENT_BONE_BLOCK), Blocks.BONE_BLOCK.asItem()));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(JackOAmmoRecipe.INGREDIENT_ENDER_PEARL), Items.ENDER_PEARL));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(JackOAmmoRecipe.INGREDIENT_FIREWORK_ROCKET), Items.FIREWORK_ROCKET));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(Ingredient.fromItems(Items.SPLASH_POTION)), Items.SPLASH_POTION));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(Ingredient.fromItems(Items.LINGERING_POTION)), Items.LINGERING_POTION));
        recipes.add(new RecipeJackOAmmoDummy(Collections.singletonList(Ingredient.fromItems(Items.ARROW, Items.SPECTRAL_ARROW, Items.TIPPED_ARROW)), Items.ARROW));
        registration.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }
}

