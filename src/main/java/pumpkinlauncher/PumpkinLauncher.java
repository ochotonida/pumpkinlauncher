package pumpkinlauncher;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pumpkinlauncher.proxy.CommonProxy;

import javax.annotation.Nonnull;

@Mod(modid=PumpkinLauncher.MODID, name=PumpkinLauncher.MODNAME, version=PumpkinLauncher.MODVERSION, updateJSON="https://github.com/ochotonida/pumpkinlauncher/blob/master/update.json")
public class PumpkinLauncher {

    public static final String MODID = "pumpkinlauncher";
    public static final String MODNAME = "Jack-O'-Launcher";
    public static final String MODVERSION = "1.12.2-0.0.0";

    public static final CreativeTabs CREATIVE_TAB = new ModTab();

    @SidedProxy(serverSide = "pumpkinlauncher.proxy.CommonProxy", clientSide = "pumpkinlauncher.proxy.ClientProxy")
    public static CommonProxy proxy;

    @net.minecraftforge.fml.common.Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ModItems.register(event.getRegistry());
        }
        @SubscribeEvent
        public static void registerItems(ModelRegistryEvent event) {
            ModItems.registerModels();
        }
    }

    private static class ModTab extends CreativeTabs {

        private ModTab() {
            super(PumpkinLauncher.MODID + ".tools");
        }

        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.PUMPKIN);
        }

    }
}
