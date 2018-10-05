package pumpkinlauncher.proxy;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.entity.EntityPumkinProjectile;

public class CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) { }

    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(PumpkinLauncher.MODID, "pumkinprojectile"), EntityPumkinProjectile.class, "pumpkinprojectile", 1, PumpkinLauncher.instance, 64, 3, true);
    }
}
