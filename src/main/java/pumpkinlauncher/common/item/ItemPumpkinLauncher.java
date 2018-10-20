package pumpkinlauncher.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import pumpkinlauncher.PumpkinLauncher;
import pumpkinlauncher.common.entity.EntityPumpkinProjectile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemPumpkinLauncher extends Item {

    public ItemPumpkinLauncher() {
        setRegistryName("pumpkinlauncher");
        setUnlocalizedName("pumpkinlauncher");
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxStackSize(1);
        setMaxDamage(95);
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        int infinityLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, player.getHeldItem(hand));

        ItemStack stack = findAmmo(player);
        if (stack.isEmpty() && (player.capabilities.isCreativeMode || infinityLevel > 0)){
            stack = new ItemStack(PumpkinLauncher.PUMPKIN_AMMO);
        }
        if (!stack.isEmpty()) {
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.NEUTRAL, 1.0F, 0.6F);

                int reloadingSpeed = 40 - 6 * EnchantmentHelper.getEnchantmentLevel(PumpkinLauncher.RELOADING_SPEED, player.getHeldItem(hand));
                player.getCooldownTracker().setCooldown(this, reloadingSpeed);

                int power = 2;
                int bounceAmount = 0;
                boolean isFiery = false;
                boolean canDestroyBlocks = true;
                boolean shouldHurtPlayer = EnchantmentHelper.getEnchantmentLevel(PumpkinLauncher.BLAST_SHIELD, player.getHeldItem(hand)) == 0;
                NBTTagCompound fireworkCompound = null;
                ItemStack potionStack = null;

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
                    if (stack.getTagCompound().hasKey("potionTag")) {
                        potionStack = new ItemStack(stack.getTagCompound().getCompoundTag("potionTag"));
                    }
                }

                float velocity = 1.3F + 0.12F * EnchantmentHelper.getEnchantmentLevel(PumpkinLauncher.LAUNCHING, player.getHeldItem(hand));
                EntityPumpkinProjectile projectile = new EntityPumpkinProjectile(world, player, power, bounceAmount, isFiery, canDestroyBlocks, shouldHurtPlayer, fireworkCompound, potionStack);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, velocity, 5F);
                world.spawnEntity(projectile);

                if (!player.capabilities.isCreativeMode && itemRand.nextInt(1 + EnchantmentHelper.getEnchantmentLevel(PumpkinLauncher.AMMO_SAVING, player.getHeldItem(hand))) == 0) {
                    stack.shrink(1);
                }
            }
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
    public boolean isFull3D() {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 4;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
        return enchantment == Enchantments.MENDING || enchantment == Enchantments.UNBREAKING || enchantment == PumpkinLauncher.AMMO_SAVING || enchantment == PumpkinLauncher.RELOADING_SPEED || enchantment == PumpkinLauncher.BLAST_SHIELD || enchantment == PumpkinLauncher.LAUNCHING;
    }
}
