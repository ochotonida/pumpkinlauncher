package pumpkinlauncher.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import pumpkinlauncher.entity.EntityPumkinProjectile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemPumpkinLauncher extends Item {

    public ItemPumpkinLauncher() {
        setRegistryName("pumpkinlauncher");
        setUnlocalizedName("pumpkinlauncher");
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxStackSize(1);
        this.setMaxDamage(0);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound nbtTagCompound = stack.getTagCompound();

        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setBoolean("Loaded", false);
            stack.setTagCompound(nbtTagCompound);
        }

        if (nbtTagCompound.getBoolean("Loaded")) {
            nbtTagCompound.setBoolean("Loaded", false);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.8F, 0.3F);

            if (!world.isRemote) {
                EntityPumkinProjectile projectile = new EntityPumkinProjectile(world, player);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, 1.1F, 2.5F);
                world.spawnEntity(projectile);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            nbtTagCompound.setBoolean("Loaded", true);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.8F, 2.0F);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
    }
}
