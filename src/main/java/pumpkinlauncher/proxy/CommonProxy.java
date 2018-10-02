package pumpkinlauncher.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pumpkinlauncher.ModEntities;

public class CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) { }

    public void preInit(FMLPreInitializationEvent event) {
        ModEntities.init();
    }
}
