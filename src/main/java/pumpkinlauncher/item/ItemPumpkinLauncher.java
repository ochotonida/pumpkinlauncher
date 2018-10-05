package pumpkinlauncher.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
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
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.NEUTRAL, 1.0F, 0.6F);
            player.getCooldownTracker().setCooldown(this, 30);
            EntityPumkinProjectile projectile = new EntityPumkinProjectile(world, player);
            projectile.shoot(player, player.rotationPitch, player.rotationYaw, 1.3F, 5F);
            world.spawnEntity(projectile);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean isFull3D() {
        return true;
    }
}
