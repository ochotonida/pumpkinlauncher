package jackolauncher;

import jackolauncher.enchantment.EnchantmentBlastShield;
import jackolauncher.enchantment.EnchantmentLaunching;
import jackolauncher.enchantment.EnchantmentReloading;
import jackolauncher.enchantment.EnchantmentUnwasting;
import jackolauncher.entity.EntityJackOProjectile;
import jackolauncher.item.BehaviorDispenseJackOAmmo;
import jackolauncher.item.ItemJackOAmmo;
import jackolauncher.item.ItemJackOLauncher;
import jackolauncher.item.RecipeJackOAmmo;
import net.minecraft.block.BlockDispenser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static jackolauncher.JackOLauncher.MODID;

@Mod(MODID)
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JackOLauncher {

    public static final String MODID = "jack_o_launcher";

    public static final Item JACK_O_LAUNCHER = new ItemJackOLauncher();
    public static final Item JACK_O_AMMO = new ItemJackOAmmo();

    public static final EntityType<EntityJackOProjectile> JACK_O_PROJECTILE_ENTITY_TYPE = EntityType.register(MODID + ":jack_o_projectile", EntityType.Builder.create(EntityJackOProjectile.class, EntityJackOProjectile::new).tracker(128, 1, true));

    public static final RecipeSerializers.SimpleSerializer<RecipeJackOAmmo> CRAFTING_SPECIAL_JACK_O_AMMO = RecipeSerializers.register(new RecipeSerializers.SimpleSerializer<>("jack_o_launcher:crafting_special_jack_o_ammo", RecipeJackOAmmo::new));

    public static final Enchantment UNWASTING = new EnchantmentUnwasting();
    public static final Enchantment RELOADING = new EnchantmentReloading();
    public static final Enchantment BLAST_SHIELD = new EnchantmentBlastShield();
    public static final Enchantment LAUNCHING = new EnchantmentLaunching();

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        BlockDispenser.registerDispenseBehavior(JackOLauncher.JACK_O_AMMO, new BehaviorDispenseJackOAmmo());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(JACK_O_LAUNCHER, JACK_O_AMMO);
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(UNWASTING, RELOADING, BLAST_SHIELD, LAUNCHING);
    }
}
