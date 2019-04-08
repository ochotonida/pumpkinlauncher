package jackolauncher.item;

import jackolauncher.JackOLauncher;
import jackolauncher.entity.EntityJackOProjectile;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemJackOLauncher extends Item {

    public static final List<Enchantment> VALID_ENCHANTMENTS = Arrays.asList(Enchantments.MENDING, Enchantments.UNBREAKING, Enchantments.LOOTING, JackOLauncher.UNWASTING, JackOLauncher.BLAST_SHIELD, JackOLauncher.LAUNCHING, JackOLauncher.RELOADING);

    public ItemJackOLauncher() {
        super(new Properties().defaultMaxDamage(95).group(ItemGroup.COMBAT));
        setRegistryName(JackOLauncher.MODID, "jack_o_launcher");
    }

    private ItemStack findAmmo(EntityPlayer player) {
        if (player.getHeldItem(EnumHand.OFF_HAND).getItem() == JackOLauncher.JACK_O_AMMO) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        } else if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == JackOLauncher.JACK_O_AMMO) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() == JackOLauncher.JACK_O_AMMO) {
                    return stack;
                }
            }
            if (player.abilities.isCreativeMode) {
                return new ItemStack(JackOLauncher.JACK_O_AMMO);
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = findAmmo(player);

        if (!stack.isEmpty()) {
            if (!world.isRemote) {
                player.getCooldownTracker().setCooldown(this, 40 - 6 * EnchantmentHelper.getEnchantmentLevel(JackOLauncher.RELOADING, player.getHeldItem(hand)));
                EntityJackOProjectile projectile = new EntityJackOProjectile(world, player, stack.getOrCreateChildTag("AmmoNBT"), EnchantmentHelper.getEnchantmentLevel(JackOLauncher.BLAST_SHIELD, player.getHeldItem(hand)) == 0);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, 1.3F + 0.13F * EnchantmentHelper.getEnchantmentLevel(JackOLauncher.LAUNCHING, player.getHeldItem(hand)), 3F);
                world.spawnEntity(projectile);
                if (!player.abilities.isCreativeMode && random.nextInt(1 + EnchantmentHelper.getEnchantmentLevel(JackOLauncher.UNWASTING, player.getHeldItem(hand))) == 0) {
                    stack.shrink(1);
                }
            }
            world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.NEUTRAL, 1.0F, 0.6F);
            player.getHeldItem(hand).damageItem(1, player);
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        } else {
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.NEUTRAL, 1.0F, 1.2F);
            }
            return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }
    }

    @Override
    public int getItemEnchantability() {
        return 4;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
        return VALID_ENCHANTMENTS.contains(enchantment);
    }
}
