package jackolauncher.item.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import jackolauncher.JackOLauncher;
import jackolauncher.item.JackOAmmoHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SetRandomJackOAmmoNBT extends LootFunction {

    public static final Block[] PUMPKIN_BLOCKS = {
            Blocks.PUMPKIN,
            Blocks.CARVED_PUMPKIN,
            Blocks.JACK_O_LANTERN
    };

    public static final Block[] SPECIAL_BLOCKS = {
            Blocks.ANVIL,
            Blocks.CAKE,
            Blocks.MELON,
            Blocks.TNT,
    };

    protected SetRandomJackOAmmoNBT(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    private static void addRandomFireworks(ItemStack stack, Random random) {
        ItemStack fireworks = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundNBT compound = fireworks.getOrCreateChildTag("Fireworks");
        compound.putByte("Flight", (byte) (random.nextInt(3) + 1));

        CompoundNBT explosionCompound = new CompoundNBT();
        explosionCompound.putBoolean("Flicker", random.nextBoolean());
        explosionCompound.putBoolean("Trail", random.nextBoolean());

        List<Integer> colors = new ArrayList<>();
        int dyeAmount = 1 + random.nextInt(3);
        for (int i = 0; i < dyeAmount; i++) {
            colors.add(DyeColor.values()[random.nextInt(DyeColor.values().length)].getFireworkColor());
        }
        explosionCompound.putIntArray("Colors", colors);

        colors = new ArrayList<>();
        dyeAmount = 1 + random.nextInt(3);
        for (int i = 0; i < dyeAmount; i++) {
            colors.add(DyeColor.values()[random.nextInt(DyeColor.values().length)].getFireworkColor());
        }
        explosionCompound.putIntArray("FadeColors", colors);

        explosionCompound.putByte("Type", (byte) FireworkRocketItem.Shape.values()[random.nextInt(FireworkRocketItem.Shape.values().length)].func_196071_a());

        ListNBT explosions = new ListNBT();
        explosions.add(explosionCompound);

        compound.put("Explosions", explosions);

        JackOAmmoHelper.setFireworks(stack, fireworks);
    }

    private static void addRandomPotion(ItemStack stack, Random random) {
        List<Potion> potionTypes = new ArrayList<>(ForgeRegistries.POTION_TYPES.getValues());
        potionTypes.remove(Potions.EMPTY);
        ItemStack potion = new ItemStack(random.nextInt(4) == 0 ? Items.LINGERING_POTION : Items.SPLASH_POTION);
        PotionUtils.addPotionToItemStack(potion, potionTypes.get(random.nextInt(potionTypes.size())));
        JackOAmmoHelper.setPotion(stack, potion);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        Random random = context.getRandom();

        if (random.nextInt(12) == 0) {
            JackOAmmoHelper.setBlockState(stack, SPECIAL_BLOCKS[random.nextInt(SPECIAL_BLOCKS.length)].getDefaultState());
        } else {
            JackOAmmoHelper.setBlockState(stack, PUMPKIN_BLOCKS[random.nextInt(PUMPKIN_BLOCKS.length)].getDefaultState());
        }

        if (random.nextInt(3) == 0) {
            JackOAmmoHelper.setBouncesAmount(stack, 1);
        }

        if (random.nextInt(4) == 0) {
            JackOAmmoHelper.setBoneMeal(stack);
        }

        if (random.nextInt(4) == 0) {
            JackOAmmoHelper.setExtraDamage(stack, random.nextInt(5) + 2);
        }

        if (random.nextInt(3) == 0) {
            addRandomFireworks(stack, random);
        }

        if (random.nextBoolean()) {
            JackOAmmoHelper.setExplosionPower(stack, random.nextInt(9) + 4);

            switch (random.nextInt(6)) {
                case 0:
                    JackOAmmoHelper.setFortuneLevel(stack, random.nextInt(4) + 1);
                    break;
                case 1:
                    JackOAmmoHelper.setSilkTouch(stack);
                    break;
                case 2:
                    JackOAmmoHelper.setShouldDamageTerrain(stack, false);
            }

            if (random.nextInt(3) == 0) {
                JackOAmmoHelper.setFlaming(stack);
            }
        } else {
            JackOAmmoHelper.setExplosionPower(stack, 0);
            if (random.nextInt(3) == 0) {
                JackOAmmoHelper.setEnderPearl(stack);
            } else if (random.nextBoolean()) {
                JackOAmmoHelper.setArrows(stack, new ItemStack(Items.ARROW, 3 + random.nextInt(16)));
            }

            if (random.nextBoolean()) {
                addRandomPotion(stack, random);
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetRandomJackOAmmoNBT> {

        public Serializer() {
            super(new ResourceLocation(JackOLauncher.MODID, "set_random_jack_o_ammo_nbt"), SetRandomJackOAmmoNBT.class);
        }

        @Override
        public void serialize(JsonObject object, SetRandomJackOAmmoNBT function, JsonSerializationContext serializationContext) {

        }

        @Override
        public SetRandomJackOAmmoNBT deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditions) {
            return new SetRandomJackOAmmoNBT(conditions);
        }
    }

}
