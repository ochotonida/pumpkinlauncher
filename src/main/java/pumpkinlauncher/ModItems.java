package pumpkinlauncher;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import pumpkinlauncher.item.ItemPumpkinLauncher;

public class ModItems {

    public static final ItemPumpkinLauncher PUMPKIN_LAUNCHER = new ItemPumpkinLauncher();

    public static void register(IForgeRegistry<Item> registry) {
        registry.register(PUMPKIN_LAUNCHER);
    }

    public static void registerModels() {

    }
}
