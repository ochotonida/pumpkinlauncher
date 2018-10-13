package pumpkinlauncher.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.entity.EntityPumpkinProjectile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemPumpkinLauncher extends Item {

    public ItemPumpkinLauncher() {
        setRegistryName("pumpkinlauncher");
        setUnlocalizedName("pumpkinlauncher");
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxStackSize(1);
    }

    private ItemStack findAmmo(EntityPlayer player) {
        if (player.getHeldItem(EnumHand.OFF_HAND).getItem() == PumpkinLauncher.PUMPKIN_AMMO) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        } else if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == PumpkinLauncher.PUMPKIN_AMMO) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() == PumpkinLauncher.PUMPKIN_AMMO) {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = findAmmo(player);
        if (!stack.isEmpty() || player.capabilities.isCreativeMode) {
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.NEUTRAL, 1.0F, 0.6F);
                player.getCooldownTracker().setCooldown(this, 20);

                int power = 3;
                int bounceAmount = 0;
                boolean isFiery = false;
                boolean canDestroyBlocks = true;
                NBTTagCompound fireworkCompound = null;
                if (!stack.isEmpty() && stack.getTagCompound() != null) {
                    if (stack.getTagCompound().hasKey("power")) {
                        power = stack.getTagCompound().getByte("power");
                    }
                    if (stack.getTagCompound().hasKey("bounceAmount")) {
                        bounceAmount = stack.getTagCompound().getByte("bounceAmount");
                    }
                    if (stack.getTagCompound().hasKey("isFiery")) {
                        isFiery = stack.getTagCompound().getBoolean("isFiery");
                    }
                    if (stack.getTagCompound().hasKey("canDestroyBlocks")) {
                        canDestroyBlocks = stack.getTagCompound().getBoolean("canDestroyBlocks");
                    }
                    if (stack.getTagCompound().hasKey("fireworks")) {
                        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                        fireworkCompound = stack.getTagCompound().getCompoundTag("fireworks");
                    }
                }
                EntityPumpkinProjectile projectile = new EntityPumpkinProjectile(world, player, power, bounceAmount, isFiery, canDestroyBlocks, fireworkCompound);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, 1.3F, 5F);
                world.spawnEntity(projectile);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        } else {
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.NEUTRAL, 1.0F, 1.2F);
            }
            return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }
    }

    @Override
    public boolean isFull3D() {
        return true;
    }
}
