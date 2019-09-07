package jackolauncher.compat;

import jackolauncher.JackOLauncher;
import jackolauncher.item.JackOAmmoRecipe;
// import mezz.jei.api.MethodsReturnNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeJackOAmmoDummy extends SpecialRecipe {

    private static final Ingredient INGREDIENT_PUMPKIN = Ingredient.fromItems(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN);
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;

    RecipeJackOAmmoDummy(List<Ingredient> ingredients, Item... ingredientsForOutput) {
        super(new ResourceLocation(""));
        this.ingredients = NonNullList.create();
        this.ingredients.addAll(ingredients);
        this.ingredients.add(0, INGREDIENT_PUMPKIN);
        ArrayList<Item> ingredientsForOutputList = new ArrayList<>(Arrays.asList(ingredientsForOutput));
        ingredientsForOutputList.add(Blocks.PUMPKIN.asItem());
        this.output = JackOAmmoRecipe.getCraftingResult(ingredientsForOutputList.stream().map(ItemStack::new).toArray(ItemStack[]::new));
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return true;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(JackOLauncher.MODID, "");
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        // noinspection ConstantConditions
        return null;
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.CRAFTING;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }
}
