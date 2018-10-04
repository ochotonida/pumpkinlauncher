package pumpkinlauncher.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.8F, 1.6F);

        if (!world.isRemote) {
            EntityPumkinProjectile projectile = new EntityPumkinProjectile(world, entity);
            projectile.shoot(entity, entity.rotationPitch, entity.rotationYaw, 1.1F, 2.5F);
            world.spawnEntity(projectile);
        }
        return super.onItemUseFinish(stack, world, entity);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 20;
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
}
