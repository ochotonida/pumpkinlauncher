package jackolauncher.item;

import com.google.common.collect.Lists;
import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemJackOAmmo extends Item {

    public ItemJackOAmmo() {
        super(new Properties().group(ItemGroup.COMBAT));
        setRegistryName(JackOLauncher.MODID, "jack_o_ammo");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, worldIn, tooltip, flag);
        NBTTagCompound ammoNBT = stack.getOrCreateChildTag("AmmoNBT");

        if (!ammoNBT.getCompound("ArrowsNBT").isEmpty()) {
            ItemStack ArrowItemStack = ItemStack.read(ammoNBT.getCompound("ArrowsNBT"));
            if (ArrowItemStack.getCount() > 0) {
                tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.arrows").appendText(" " + ArrowItemStack.getCount()).applyTextStyle(TextFormatting.GRAY));

                if (ArrowItemStack.getItem() instanceof ItemTippedArrow) {
                    List<PotionEffect> potionEffects = PotionUtils.getEffectsFromStack(ArrowItemStack);
                    if (potionEffects.isEmpty()) {
                        tooltip.add(new TextComponentString("  ").appendSibling(new TextComponentTranslation("effect.none")).applyTextStyle(TextFormatting.GRAY));
                    } else {
                        for (PotionEffect potionEffect : potionEffects) {
                            ITextComponent potionEffectTextComponent = new TextComponentTranslation(potionEffect.getEffectName());

                            if (potionEffect.getAmplifier() > 0) {
                                potionEffectTextComponent.appendText(" ").appendSibling(new TextComponentTranslation("potion.potency." + potionEffect.getAmplifier()));
                            }

                            if (potionEffect.getDuration() > 20) {
                                potionEffectTextComponent.appendText(" (" + PotionUtil.getPotionDurationString(potionEffect, 0.125F) + ")");
                            }

                            if (potionEffect.getPotion().isBadEffect()) {
                                tooltip.add(new TextComponentString("  ").appendSibling(potionEffectTextComponent).applyTextStyle(TextFormatting.RED));
                            } else {
                                tooltip.add(new TextComponentString("  ").appendSibling(potionEffectTextComponent).applyTextStyle(TextFormatting.BLUE));
                            }
                        }
                    }
                } else if (ArrowItemStack.getItem() instanceof ItemSpectralArrow) {
                    tooltip.add(new TextComponentString("  ").appendSibling(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.spectral")).applyTextStyle(TextFormatting.AQUA));
                } else if (ArrowItemStack.getItem() != Items.ARROW) {
                    tooltip.add(new TextComponentString("  ").appendSibling(ArrowItemStack.getTextComponent()).applyTextStyle(TextFormatting.DARK_GREEN));
                }
            }
        }
        if (ammoNBT.getByte("ExplosionPower") > 0) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.explosion_power").appendText(" " + ammoNBT.getByte("ExplosionPower")).applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.getByte("BouncesAmount") > 0) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.bounce").appendText(" " + ammoNBT.getByte("BouncesAmount")).applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.getByte("ExtraDamage") > 0) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.extra_damage").appendText(" " + ammoNBT.getByte("ExtraDamage")).applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.getBoolean("IsFlaming")) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.flaming").applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.hasKey("CanDestroyBlocks") && !ammoNBT.getBoolean("CanDestroyBlocks")) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.cannot_destroy_blocks").applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.getBoolean("HasBoneMeal")) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.bone_meal").applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.getBoolean("IsEnderPearl")) {
            tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.ender_pearl").applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.hasKey("FireworksNBT")) {
            NBTTagCompound fireworksNBT = (NBTTagCompound) ammoNBT.getTag("FireworksNBT");
            if (fireworksNBT.hasKey("Flight")) {
                tooltip.add(new TextComponentTranslation("item.minecraft.firework_rocket.flight").appendText(" " + fireworksNBT.getByte("Flight")).applyTextStyle(TextFormatting.GRAY));
            }

            NBTTagList fireworkExplosionsNBTList = fireworksNBT.getList("Explosions", 10);
            if (!fireworkExplosionsNBTList.isEmpty()) {
                for (int i = 0; i < fireworkExplosionsNBTList.size(); ++i) {
                    NBTTagCompound fireworkExplosionNBT = fireworkExplosionsNBTList.getCompound(i);
                    List<ITextComponent> fireworkExplosionTextComponents = Lists.newArrayList();
                    ItemFireworkStar.func_195967_a(fireworkExplosionNBT, fireworkExplosionTextComponents);

                    if (!fireworkExplosionTextComponents.isEmpty()) {
                        for (int j = 1; j < fireworkExplosionTextComponents.size(); ++j) {
                            fireworkExplosionTextComponents.set(j, new TextComponentString("  ").appendSibling(fireworkExplosionTextComponents.get(j)));
                        }
                        tooltip.addAll(fireworkExplosionTextComponents);
                    }
                }
            }
        }
        if (ammoNBT.hasKey("PotionNBT")) {
            ItemStack potionStack = ItemStack.read(ammoNBT.getCompound("PotionNBT"));
            if (potionStack.getItem() == Items.LINGERING_POTION) {
                tooltip.add(new TextComponentTranslation("item.jack_o_launcher.jack_o_ammo.lingering").applyTextStyle(TextFormatting.DARK_PURPLE));
            }
            PotionUtils.addPotionTooltip(potionStack, tooltip, potionStack.getItem() == Items.LINGERING_POTION ? 0.25F : 1);
        }
    }
}
