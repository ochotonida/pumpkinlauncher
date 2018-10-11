package pumpkinlauncher.integration;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class PumpkinAmmoRecipeWrapper implements IRecipeWrapper {

    private final List<ItemStack> inputs;
    private final ItemStack output;

    PumpkinAmmoRecipeWrapper(ItemStack output, ItemStack... inputs) {
        this.inputs = Arrays.asList(inputs);
        this.output = output;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }
}
