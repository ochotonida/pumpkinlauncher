package pumpkinlauncher.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pumpkinlauncher.entity.EntityPumkinProjectile;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.entity.RenderPumpkinProjectile;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(PumpkinLauncher.MODID + ":" + id, "inventory"));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(EntityPumkinProjectile.class, RenderPumpkinProjectile.FACTORY);
    }

}
