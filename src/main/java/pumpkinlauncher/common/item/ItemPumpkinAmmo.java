package pumpkinlauncher.common.item;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class ItemPumpkinAmmo extends Item {

    public ItemPumpkinAmmo() {
        setRegistryName("pumpkinammo");
        setUnlocalizedName("pumpkinammo");
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound != null) {
            if (nbttagcompound.hasKey("arrowTag") && !nbttagcompound.getCompoundTag("arrowTag").hasNoTags()) {
                ItemStack arrowStack = new ItemStack(nbttagcompound.getCompoundTag("arrowTag"));

                tooltip.add(I18n.translateToLocal("item.pumpkinammo.arrows") + " " + arrowStack.getCount());

                if (arrowStack.getItem() instanceof ItemTippedArrow) {
                    List<PotionEffect> list = PotionUtils.getEffectsFromStack(arrowStack);
                    if (list.isEmpty()) {
                        tooltip.add(TextFormatting.GRAY + "  " + I18n.translateToLocal("effect.none").trim());
                    } else {
                        for (PotionEffect potioneffect : list) {
                            String effectString = I18n.translateToLocal(potioneffect.getEffectName()).trim();

                            if (potioneffect.getAmplifier() > 0) {
                                effectString = effectString + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                            }

                            if (potioneffect.getDuration() > 20) {
                                effectString = effectString + " (" + Potion.getPotionDurationString(potioneffect, 0.125F) + ")";
                            }

                            if (potioneffect.getPotion().isBadEffect()) {
                                tooltip.add(TextFormatting.RED + "  " + effectString);
                            }
                            else {
                                tooltip.add(TextFormatting.BLUE + "  " + effectString);
                            }
                        }
                    }
                } else if (arrowStack.getItem() instanceof ItemSpectralArrow) {
                    tooltip.add(TextFormatting.AQUA + "  " + I18n.translateToLocal("item.pumpkinammo.spectral"));
                } else if (arrowStack.getItem() != Items.ARROW) {
                    tooltip.add("  " + arrowStack.getTextComponent().getUnformattedText());
                }
            }
            if (nbttagcompound.hasKey("power") && nbttagcompound.getByte("power") > 0) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.power") + " " + nbttagcompound.getByte("power"));
            }
            if (nbttagcompound.hasKey("bounceAmount") && nbttagcompound.getByte("bounceAmount") > 0) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.bounce") + " " + nbttagcompound.getByte("bounceAmount"));
            }
            if (nbttagcompound.hasKey("extraDamage") && nbttagcompound.getByte("extraDamage") > 0) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.extradamage") + " " + nbttagcompound.getByte("extraDamage"));
            }
            if (nbttagcompound.hasKey("isFiery") && nbttagcompound.getBoolean("isFiery")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.isfiery"));
            }
            if (nbttagcompound.hasKey("canDestroyBlocks") && !nbttagcompound.getBoolean("canDestroyBlocks")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.nogriefing"));
            }
            if (nbttagcompound.hasKey("hasBonemeal") && nbttagcompound.getBoolean("hasBonemeal")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.bonemeal"));
            }
            if (nbttagcompound.hasKey("isEnderPearl") && nbttagcompound.getBoolean("isEnderPearl")) {
                tooltip.add(I18n.translateToLocal("item.pumpkinammo.enderpearl"));
            }
            if (nbttagcompound.hasKey("fireworks")) {
                NBTTagCompound fireworksCompound = (NBTTagCompound) nbttagcompound.getTag("fireworks");
                if (fireworksCompound.hasKey("Flight", 99)) {
                    tooltip.add(I18n.translateToLocal("item.fireworks.flight") + " " + fireworksCompound.getByte("Flight"));
                }

                NBTTagList nbttaglist = fireworksCompound.getTagList("Explosions", 10);
                if (!nbttaglist.hasNoTags()) {
                    for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                        NBTTagCompound subCompound = nbttaglist.getCompoundTagAt(i);
                        List<String> list = Lists.newArrayList();
                        ItemFireworkCharge.addExplosionInfo(subCompound, list);

                        if (!list.isEmpty()) {
                            for (int j = 1; j < list.size(); ++j) {
                                list.set(j, "  " + list.get(j));
                            }
                            tooltip.addAll(list);
                        }
                    }
                }
            }
            if (nbttagcompound.hasKey("potionTag")) {
                ItemStack potionStack = new ItemStack(stack.getTagCompound().getCompoundTag("potionTag"));
                PotionUtils.addPotionTooltip(potionStack, tooltip, potionStack.getItem() == Items.LINGERING_POTION ? 0.25F : 1);
                if (potionStack.getItem() == Items.LINGERING_POTION) {
                    tooltip.add(I18n.translateToLocal("item.pumpkinammo.lingering"));
                }
            }
        }
    }
}
