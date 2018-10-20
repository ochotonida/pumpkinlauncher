package pumpkinlauncher.common;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.common.entity.EntityPumpkinProjectile;
import pumpkinlauncher.common.item.BehaviorDispensePumpkinAmmo;

public class CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) { }

    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(PumpkinLauncher.MODID, "pumkinprojectile"), EntityPumpkinProjectile.class, "pumpkinprojectile", 1, PumpkinLauncher.instance, 64, 3, true);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(PumpkinLauncher.PUMPKIN_AMMO, new BehaviorDispensePumpkinAmmo());
    }

    public void init(FMLInitializationEvent event) { }
}
