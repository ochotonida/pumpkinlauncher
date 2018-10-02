package pumpkinlauncher;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import pumpkinlauncher.entity.EntityPumkinProjectile;

public class ModEntities {

    public static void init() {
        EntityRegistry.registerModEntity(new ResourceLocation(PumpkinLauncher.MODID, "pumkinprojectile"), EntityPumkinProjectile.class, "pumpkinprojectile", 1, PumpkinLauncher.instance, 64, 3, true);
    }

}
