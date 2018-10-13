package pumpkinlauncher.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pumpkinlauncher.entity.EntityPumpkinProjectile;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.entity.RenderPumpkinProjectile;
import pumpkinlauncher.handler.ClientEventHandler;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(PumpkinLauncher.MODID + ":" + id, "inventory"));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(EntityPumpkinProjectile.class, RenderPumpkinProjectile.FACTORY);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientEventHandler handler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
    }

}
