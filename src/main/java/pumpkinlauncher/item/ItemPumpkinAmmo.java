package pumpkinlauncher.item;

import mcp.MethodsReturnNonnullByDefault;
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
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemPumpkinAmmo extends Item {

    public ItemPumpkinAmmo() {
        this.setRegistryName("pumpkinammo");
        this.setUnlocalizedName("pumpkinammo");
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public String getHighlightTip( ItemStack item, String displayName )
    {
        return displayName;
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
            if (nbttagcompound.hasKey("bounceAmount") && nbttagcompound.getByte("bounceAmount") > 0) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.bounce") + " " + nbttagcompound.getByte("bounceAmount"));
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
