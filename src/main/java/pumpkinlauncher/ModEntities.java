package pumpkinlauncher;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.client.renderer.RenderPumpkinProjectile;
import pumpkinlauncher.entity.EntityPumkinProjectile;

public class ModEntities {

    public static void init() {
        EntityRegistry.registerModEntity(new ResourceLocation(PumpkinLauncher.MODID, "pumkinprojectile"), EntityPumkinProjectile.class, "pumpkinprojectile", 1, PumpkinLauncher.instance, 64, 3, true);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPumkinProjectile.class, RenderPumpkinProjectile.FACTORY);
    }
}
