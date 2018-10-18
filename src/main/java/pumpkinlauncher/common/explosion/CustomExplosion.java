package pumpkinlauncher.common.explosion;

import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@MethodsReturnNonnullByDefault
public class CustomExplosion extends Explosion {

    protected final boolean shouldCauseFire;
    protected final boolean shouldDamageTerrain;
    protected final boolean shouldDamageShooter;
    protected final Random random;
    protected final World world;
    protected final double x;
    protected final double y;
    protected final double z;
    protected final Entity exploder;
    protected final EntityLivingBase shootingEntity;
    protected final float size;
    protected static boolean shouldFireExplosionEvent = true;

    protected CustomExplosion(World world, Entity explodingEntity, @Nullable EntityLivingBase shootingEntity, double x, double y, double z, float power, boolean isFlaming, boolean isSmoking, boolean shouldHurtShootingEntity) {
        super(world, explodingEntity, x, y, z, power, isFlaming, isSmoking);
        this.random = new Random();
        this.world = world;
        this.exploder = explodingEntity;
        this.shootingEntity = shootingEntity;
        this.size = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.shouldCauseFire = isFlaming;
        this.shouldDamageTerrain = isSmoking;
        this.shouldDamageShooter = shouldHurtShootingEntity;
    }

    public static void createExplosion(World world, Entity explodingEntity, @Nullable EntityLivingBase shootingEntity, double x, double y, double z, float power, boolean isFlaming, boolean isSmoking, boolean shouldHurtShootingEntity) {
        CustomExplosion explosion = new CustomExplosion(world, explodingEntity, shootingEntity, x, y, z, power, isFlaming, isSmoking, shouldHurtShootingEntity);

        if (shouldFireExplosionEvent && net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) {
            return;
        }

        explosion.doExplosionA();

        if (world instanceof WorldServer) {
            explosion.doExplosionB(false);
            if (!isSmoking) {
                explosion.clearAffectedBlockPositions();
            }

            for (EntityPlayer entityplayer : world.playerEntities) {
                if (entityplayer.getDistanceSq(x, y, z) < 4096) {
                    ((EntityPlayerMP) entityplayer).connection.sendPacket(new SPacketExplosion(x, y, z, power, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityplayer)));
                }
            }
        } else {
            explosion.doExplosionB(true);
        }
    }

    @Override
    public void doExplosionA() {
        calculateAffectedBlockPositions();
        damageEntities();
    }

    @SuppressWarnings("ConstantConditions")
    protected void calculateAffectedBlockPositions() {
        Set<BlockPos> affectedBlockPositions = Sets.newHashSet();
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (j / 15F * 2 - 1);
                        double d1 = (k / 15F * 2 - 1);
                        double d2 = (l / 15F * 2 - 1);
                        double distance = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / distance;
                        d1 = d1 / distance;
                        d2 = d2 / distance;
                        float f = this.size * (0.7F + world.rand.nextFloat() * 0.6F);
                        double x = this.x;
                        double y = this.y;
                        double z = this.z;

                        for (; f > 0; f -= 0.225)
                        {
                            BlockPos pos = new BlockPos(x, y, z);
                            IBlockState state = this.world.getBlockState(pos);

                            if (state.getMaterial() != Material.AIR) {
                                float f2 = exploder != null ? exploder.getExplosionResistance(this, world, pos, state) : state.getBlock().getExplosionResistance(world, pos, null, this);
                                f -= (f2 + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && (exploder == null || exploder.canExplosionDestroyBlock(this, world, pos, state, f))) {
                                affectedBlockPositions.add(pos);
                            }

                            x += d0 * 0.3;
                            y += d1 * 0.3;
                            z += d2 * 0.3;
                        }
                    }
                }
            }
        }
        getAffectedBlockPositions().addAll(affectedBlockPositions);
    }

    protected void damageEntities() {
        float size = this.size * 2;

        List<Entity> affectedEntities = world.getEntitiesWithinAABBExcludingEntity(exploder, new AxisAlignedBB(MathHelper.floor(x - size - 1), MathHelper.floor(y - size - 1), MathHelper.floor(z - size - 1), MathHelper.floor(x + size + 1), MathHelper.floor(y + size + 1), MathHelper.floor(z + size + 1)));
        if (shouldFireExplosionEvent) {
            net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(world, this, affectedEntities, size);
        }
        Vec3d vec3d = new Vec3d(x, y, z);

        for (Entity entity : affectedEntities) {
            if (!entity.isImmuneToExplosions()) {
                double d12 = entity.getDistance(x, y, z) / size;

                if (d12 <= 1) {
                    double distanceX = entity.posX - x;
                    double distanceY = entity.posY + entity.getEyeHeight() - y;
                    double distanceZ = entity.posZ - z;
                    double distance = MathHelper.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

                    if (distance != 0) {
                        distanceX = distanceX / distance;
                        distanceY = distanceY / distance;
                        distanceZ = distanceZ / distance;
                        double blockDensity = world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                        double damageMultiplier = (1 - d12) * blockDensity;
                        damageMultiplier = damageMultiplier * damageMultiplier + damageMultiplier;
                        if (entity == shootingEntity && !shouldDamageShooter) {
                            damageMultiplier /= 32;
                        }
                        entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), (float) ((int) (damageMultiplier / 2 * 7 * size + 1)));
                        double knockbackMultiplier = damageMultiplier;

                        if (entity instanceof EntityLivingBase) {
                            knockbackMultiplier = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, damageMultiplier);
                        }

                        entity.motionX += distanceX * knockbackMultiplier;
                        entity.motionY += distanceY * knockbackMultiplier;
                        entity.motionZ += distanceZ * knockbackMultiplier;

                        if (entity instanceof EntityPlayer) {
                            EntityPlayer entityplayer = (EntityPlayer) entity;

                            if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying)) {
                                getPlayerKnockbackMap().put(entityplayer, new Vec3d(distanceX * damageMultiplier, distanceY * damageMultiplier, distanceZ * damageMultiplier));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doExplosionB(boolean spawnParticles) {
        this.world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

        if (spawnParticles) {
            spawnParticles();
        }

        if (this.shouldDamageTerrain) {
            for (BlockPos blockpos : getAffectedBlockPositions()) {
                IBlockState state = world.getBlockState(blockpos);
                Block block = state.getBlock();

                if (spawnParticles) {
                    spawnParticles(blockpos);
                }

                if (state.getMaterial() != Material.AIR) {
                    if (block.canDropFromExplosion(this)) {
                        block.dropBlockAsItemWithChance(world, blockpos, world.getBlockState(blockpos), 1.0F / size, 0);
                    }
                    block.onBlockExploded(world, blockpos, this);
                }
            }
        }

        if (this.shouldCauseFire)
        {
            for (BlockPos pos : getAffectedBlockPositions())
            {
                if (this.world.getBlockState(pos).getMaterial() == Material.AIR && this.world.getBlockState(pos.down()).isFullBlock() && this.random.nextInt(3) == 0)
                {
                    this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    protected void spawnParticles(BlockPos pos) {
        double d0 = pos.getX() + world.rand.nextFloat();
        double d1 = pos.getY() + world.rand.nextFloat();
        double d2 = pos.getZ() + world.rand.nextFloat();
        double d3 = d0 - this.x;
        double d4 = d1 - this.y;
        double d5 = d2 - this.z;
        double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        d3 = d3 / d6;
        d4 = d4 / d6;
        d5 = d5 / d6;
        double d7 = 0.5D / (d6 / (double)this.size + 0.1D);
        d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
        d3 = d3 * d7;
        d4 = d4 * d7;
        d5 = d5 * d7;
        this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
        this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
    }

    protected void spawnParticles() {
        if (this.size >= 2.0F && this.shouldDamageTerrain) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        } else {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        }
    }

    @Override
    @Nullable
    public EntityLivingBase getExplosivePlacedBy() {
        return shootingEntity != null ? shootingEntity : super.getExplosivePlacedBy();
    }
}
