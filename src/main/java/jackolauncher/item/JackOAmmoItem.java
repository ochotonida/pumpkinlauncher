package jackolauncher.item;

import com.google.common.collect.Lists;
import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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

        tooltip.add(JackOAmmoHelper.getBlockState(stack).getBlock().getNameTextComponent().applyTextStyle(TextFormatting.GRAY));

        if (JackOAmmoHelper.hasSilkTouch(stack)) {
            tooltip.add(Enchantments.SILK_TOUCH.getDisplayName(1).applyTextStyle(TextFormatting.GRAY));
        }

        int fortuneLevel = JackOAmmoHelper.getFortuneLevel(stack);
        if (fortuneLevel > 0) {
            tooltip.add(Enchantments.FORTUNE.getDisplayName(fortuneLevel).applyTextStyle(TextFormatting.GRAY));
        }

        ItemStack arrowStack = JackOAmmoHelper.getArrows(stack);
        if (!arrowStack.isEmpty()) {
            if (arrowStack.getCount() > 0) {
                tooltip.add(new TranslationTextComponent("item.jack_o_launcher.jack_o_ammo.arrows").appendText(" " + arrowStack.getCount()).applyTextStyle(TextFormatting.GRAY));

                if (arrowStack.getItem() instanceof TippedArrowItem) {
                    List<EffectInstance> effects = PotionUtils.getEffectsFromStack(arrowStack);
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
                } else if (arrowStack.getItem() instanceof SpectralArrowItem) {
                    tooltip.add(new StringTextComponent("  ").appendSibling(new TranslationTextComponent("item.jack_o_launcher.jack_o_ammo.spectral")).applyTextStyle(TextFormatting.AQUA));
                } else if (arrowStack.getItem() != Items.ARROW) {
                    tooltip.add(new StringTextComponent("  ").appendSibling(arrowStack.getTextComponent()).applyTextStyle(TextFormatting.DARK_GREEN));
                }
            }
        }

        int explosionPower = JackOAmmoHelper.getExplosionPower(stack);
        addTranslationTextComponent(tooltip, explosionPower > 0, "explosion_power", " " + explosionPower);
        int bouncesAmount = JackOAmmoHelper.getBouncesAmount(stack);
        addTranslationTextComponent(tooltip, bouncesAmount > 0, "bounce");
        int extraDamage = JackOAmmoHelper.getExtraDamage(stack);
        addTranslationTextComponent(tooltip, extraDamage > 0, "extra_damage", " " + extraDamage);

        addTranslationTextComponent(tooltip, JackOAmmoHelper.isFlaming(stack), "flaming");
        addTranslationTextComponent(tooltip, !JackOAmmoHelper.getShouldDamageTerrain(stack), "cannot_destroy_blocks");
        addTranslationTextComponent(tooltip, JackOAmmoHelper.isBoneMeal(stack), "bone_meal");
        addTranslationTextComponent(tooltip, JackOAmmoHelper.isEnderPearl(stack), "ender_pearl");

        int flight = JackOAmmoHelper.getFlight(stack);
        if (flight > 0) {
            tooltip.add(new TranslationTextComponent("item.minecraft.firework_rocket.flight").appendText(" " + flight).applyTextStyle(TextFormatting.GRAY));
        }


        ListNBT fireworkExplosionsNBTList = JackOAmmoHelper.getFireworkExplosions(stack);
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

        ItemStack potionStack = JackOAmmoHelper.getPotion(stack);
        if (!potionStack.isEmpty()) {
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
