package pumpkinlauncher;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import pumpkinlauncher.item.ItemPumpkinLauncher;
import pumpkinlauncher.proxy.CommonProxy;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod(modid=PumpkinLauncher.MODID, name=PumpkinLauncher.MODNAME, version=PumpkinLauncher.MODVERSION, updateJSON="https://github.com/ochotonida/pumpkinlauncher/blob/master/update.json")
public class PumpkinLauncher {

    public static final String MODID = "pumpkinlauncher";
    public static final String MODNAME = "Jack-O'-Launcher";
    public static final String MODVERSION = "1.12.2-0.0.0";

    public static final ItemPumpkinLauncher PUMPKIN_LAUNCHER = new ItemPumpkinLauncher();

    @Mod.Instance
    public static PumpkinLauncher instance;

    @SidedProxy(serverSide = "pumpkinlauncher.proxy.CommonProxy", clientSide = "pumpkinlauncher.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @net.minecraftforge.fml.common.Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();
            registry.register(PUMPKIN_LAUNCHER);
        }

        @SubscribeEvent
        public static void registerItemModels(ModelRegistryEvent event) {
            PumpkinLauncher.proxy.registerItemRenderer(PUMPKIN_LAUNCHER, 0, "pumpkinlauncher");
        }
    }
}
