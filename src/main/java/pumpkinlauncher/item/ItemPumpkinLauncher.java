package pumpkinlauncher.item;

import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import pumpkinlauncher.PumpkinLauncher;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemPumpkinLauncher extends Item {

    public ItemPumpkinLauncher() {
        this.setRegistryName("pumpkinlauncher");
        this.setUnlocalizedName("pumpkinlauncher");
        this.setCreativeTab(PumpkinLauncher.CREATIVE_TAB);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        /*
        if (!playerIn.capabilities.isCreativeMode) {
            itemstack.shrink(1);
        }*/

        world.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote)
        {
            EntityFallingBlock projectile = new EntityFallingBlock(world, playerIn.posX, playerIn.posY + 3, playerIn.posZ, Blocks.PUMPKIN.getDefaultState());
            EntitySnowball entitysnowball = new EntitySnowball(world, playerIn);
            entitysnowball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(entitysnowball);
        }

        // noinspection ConstantConditions
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }
}