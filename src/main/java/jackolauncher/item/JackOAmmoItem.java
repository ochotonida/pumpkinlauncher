package jackolauncher.item;

import com.google.common.collect.Lists;
import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JackOAmmoItem extends Item {

    public JackOAmmoItem() {
        super(new Properties().group(ItemGroup.COMBAT));
        setRegistryName(JackOLauncher.MODID, "jack_o_ammo");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, worldIn, tooltip, flag);
        CompoundNBT ammoNBT = stack.getOrCreateChildTag("AmmoNBT");

        if (ammoNBT.contains("BlockState")) {
            tooltip.add(NBTUtil.readBlockState(ammoNBT.getCompound("BlockState")).getBlock().getNameTextComponent().applyTextStyle(TextFormatting.GRAY));
        }

        if (ammoNBT.getBoolean("HasSilkTouch")) {
            tooltip.add(Enchantments.SILK_TOUCH.getDisplayName(1).applyTextStyle(TextFormatting.GRAY));
        }
        if (ammoNBT.getByte("FortuneLevel") > 0) {
            tooltip.add(Enchantments.FORTUNE.getDisplayName(ammoNBT.getByte("FortuneLevel")).applyTextStyle(TextFormatting.GRAY));
        }

        if (!ammoNBT.getCompound("ArrowsNBT").isEmpty()) {
            ItemStack ArrowItemStack = ItemStack.read(ammoNBT.getCompound("ArrowsNBT"));
            if (ArrowItemStack.getCount() > 0) {
                tooltip.add(new TranslationTextComponent("item.jack_o_launcher.jack_o_ammo.arrows").appendText(" " + ArrowItemStack.getCount()).applyTextStyle(TextFormatting.GRAY));

                if (ArrowItemStack.getItem() instanceof TippedArrowItem) {
                    List<EffectInstance> effects = PotionUtils.getEffectsFromStack(ArrowItemStack);
                    if (effects.isEmpty()) {
                        tooltip.add(new StringTextComponent("  ").appendSibling(new TranslationTextComponent("effect.none")).applyTextStyle(TextFormatting.GRAY));
                    } else {
                        for (EffectInstance effect : effects) {
                            ITextComponent potionEffectTextComponent = new TranslationTextComponent(effect.getEffectName());

                            if (effect.getAmplifier() > 0) {
                                potionEffectTextComponent.appendText(" ").appendSibling(new TranslationTextComponent("potion.potency." + effect.getAmplifier()));
                            }

                            if (effect.getDuration() > 20) {
                                potionEffectTextComponent.appendText(" (" + EffectUtils.getPotionDurationString(effect, 0.125F) + ")");
                            }

                            if (effect.getPotion().isBeneficial()) {
                                tooltip.add(new StringTextComponent("  ").appendSibling(potionEffectTextComponent).applyTextStyle(TextFormatting.BLUE));
                            } else {
                                tooltip.add(new StringTextComponent("  ").appendSibling(potionEffectTextComponent).applyTextStyle(TextFormatting.RED));
                            }
                        }
                    }
                } else if (ArrowItemStack.getItem() instanceof SpectralArrowItem) {
                    tooltip.add(new StringTextComponent("  ").appendSibling(new TranslationTextComponent("item.jack_o_launcher.jack_o_ammo.spectral")).applyTextStyle(TextFormatting.AQUA));
                } else if (ArrowItemStack.getItem() != Items.ARROW) {
                    tooltip.add(new StringTextComponent("  ").appendSibling(ArrowItemStack.getTextComponent()).applyTextStyle(TextFormatting.DARK_GREEN));
                }
            }
        }

        addTranslationTextComponent(tooltip, ammoNBT.getByte("ExplosionPower") > 0, "explosion_power", " " + ammoNBT.getByte("ExplosionPower"));
        addTranslationTextComponent(tooltip, ammoNBT.getByte("BouncesAmount") > 0, "bounce");
        addTranslationTextComponent(tooltip, ammoNBT.getByte("ExtraDamage") > 0, "extra_damage", " " + ammoNBT.getByte("ExtraDamage"));
        addTranslationTextComponent(tooltip, ammoNBT.getBoolean("IsFlaming"), "flaming");
        addTranslationTextComponent(tooltip, ammoNBT.contains("CanDestroyBlocks") && !ammoNBT.getBoolean("CanDestroyBlocks"), "cannot_destroy_blocks");
        addTranslationTextComponent(tooltip, ammoNBT.getBoolean("HasBoneMeal"), "bone_meal");
        addTranslationTextComponent(tooltip, ammoNBT.getBoolean("IsEnderPearl"), "ender_pearl");

        if (ammoNBT.contains("FireworksNBT")) {
            CompoundNBT fireworksNBT = ammoNBT.getCompound("FireworksNBT");
            if (fireworksNBT.contains("Flight")) {
                tooltip.add(new TranslationTextComponent("item.minecraft.firework_rocket.flight").appendText(" " + fireworksNBT.getByte("Flight")).applyTextStyle(TextFormatting.GRAY));
            }

            ListNBT fireworkExplosionsNBTList = fireworksNBT.getList("Explosions", 10);
            if (!fireworkExplosionsNBTList.isEmpty()) {
                for (int i = 0; i < fireworkExplosionsNBTList.size(); ++i) {
                    CompoundNBT fireworkExplosionNBT = fireworkExplosionsNBTList.getCompound(i);
                    List<ITextComponent> fireworkExplosionTextComponents = Lists.newArrayList();
                    FireworkStarItem.func_195967_a(fireworkExplosionNBT, fireworkExplosionTextComponents);

                    if (!fireworkExplosionTextComponents.isEmpty()) {
                        for (int j = 1; j < fireworkExplosionTextComponents.size(); ++j) {
                            fireworkExplosionTextComponents.set(j, new StringTextComponent("  ").appendSibling(fireworkExplosionTextComponents.get(j)));
                        }
                        tooltip.addAll(fireworkExplosionTextComponents);
                    }
                }
            }
        }
        if (ammoNBT.contains("PotionNBT")) {
            ItemStack potionStack = ItemStack.read(ammoNBT.getCompound("PotionNBT"));
            if (potionStack.getItem() == Items.LINGERING_POTION) {
                tooltip.add(new TranslationTextComponent("item.jack_o_launcher.jack_o_ammo.lingering").applyTextStyle(TextFormatting.DARK_PURPLE));
            }
            PotionUtils.addPotionTooltip(potionStack, tooltip, potionStack.getItem() == Items.LINGERING_POTION ? 0.25F : 1);
        }
    }

    private void addTranslationTextComponent(List<ITextComponent> tooltip, boolean condition, String translationKey) {
        addTranslationTextComponent(tooltip, condition, translationKey, null);
    }

    private void addTranslationTextComponent(List<ITextComponent> tooltip, boolean condition, String translationKey, @Nullable String suffix) {
        if (!condition) {
            return;
        }
        TextComponent result = new TranslationTextComponent("item.jack_o_launcher.jack_o_ammo." + translationKey);
        if (suffix != null) {
            result.appendText(suffix);
        }
        result.applyTextStyle(TextFormatting.GRAY);
        tooltip.add(result);
    }
}
