package pumpkinlauncher.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPumpkinAmmo extends Item {

    public ItemPumpkinAmmo() {
        this.setRegistryName("pumpkinammo");
        this.setUnlocalizedName("pumpkinammo");
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();

        if (nbttagcompound != null) {
            if (nbttagcompound.hasKey("power")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.power") + " " + nbttagcompound.getByte("power"));
            }
            if (nbttagcompound.hasKey("isFiery") && nbttagcompound.getBoolean("isFiery")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.isfiery"));
            }
            if (nbttagcompound.hasKey("canDestroyBlocks") && !nbttagcompound.getBoolean("canDestroyBlocks")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.nogriefing"));
            }
        }
    }
}
