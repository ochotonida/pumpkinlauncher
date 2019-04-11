package pumpkinlauncher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import pumpkinlauncher.common.CommonProxy;
import pumpkinlauncher.common.enchantment.EnchantmentAmmoSaving;
import pumpkinlauncher.common.enchantment.EnchantmentBlastShield;
import pumpkinlauncher.common.enchantment.EnchantmentLaunching;
import pumpkinlauncher.common.enchantment.EnchantmentReloadingSpeed;
import pumpkinlauncher.common.item.ItemPumpkinAmmo;
import pumpkinlauncher.common.item.ItemPumpkinLauncher;
import pumpkinlauncher.common.item.RecipePumpkinAmmo;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod(modid = PumpkinLauncher.MODID, name = PumpkinLauncher.MODNAME, version = PumpkinLauncher.MODVERSION, updateJSON = "https://raw.githubusercontent.com/ochotonida/pumpkinlauncher/1.13.2/update.json")
public class PumpkinLauncher {

    public static final String MODID = "pumpkinlauncher";
    public static final String MODNAME = "Jack-O'-Launcher";
    public static final String MODVERSION = "1.12.2-1.6.3";

    public static final Item PUMPKIN_LAUNCHER = new ItemPumpkinLauncher();
    public static final Item PUMPKIN_AMMO = new ItemPumpkinAmmo();

    public static final Enchantment AMMO_SAVING = new EnchantmentAmmoSaving();
    public static final Enchantment RELOADING_SPEED = new EnchantmentReloadingSpeed();
    public static final Enchantment BLAST_SHIELD = new EnchantmentBlastShield();
    public static final Enchantment LAUNCHING = new EnchantmentLaunching();

    @Mod.Instance
    public static PumpkinLauncher instance;

    @SidedProxy(serverSide = "pumpkinlauncher.common.CommonProxy", clientSide = "pumpkinlauncher.client.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @net.minecraftforge.fml.common.Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();
            registry.register(PUMPKIN_LAUNCHER);
            registry.register(PUMPKIN_AMMO);
        }

        @SubscribeEvent
        public static void registerItemModels(ModelRegistryEvent event) {
            proxy.registerItemRenderer(PUMPKIN_LAUNCHER, 0, "pumpkinlauncher");
            proxy.registerItemRenderer(PUMPKIN_AMMO, 0, "pumpkinammo");
        }

        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
            event.getRegistry().register(new RecipePumpkinAmmo());
        }

        @SubscribeEvent
        public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().register(AMMO_SAVING);
            event.getRegistry().register(RELOADING_SPEED);
            event.getRegistry().register(BLAST_SHIELD);
            event.getRegistry().register(LAUNCHING);
        }
    }
}
