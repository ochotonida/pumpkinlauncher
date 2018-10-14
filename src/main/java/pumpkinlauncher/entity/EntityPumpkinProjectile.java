package pumpkinlauncher.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class EntityPumpkinProjectile extends Entity implements IProjectile {

    private static final DataParameter<Integer> BOUNCES_LEFT = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_FLAMING = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SMOKING = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_FIREWORK = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<NBTTagCompound> FIREWORK_NBT = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.COMPOUND_TAG);

    private int xTile;
    private int yTile;
    private int zTile;
    private int ignoreTime;
    private int lifetimeRemaining = -1;
    private Entity ignoreEntity;
    private int power;
    private boolean canDestroyBlocks;
    int rotation;

    @Nullable
    private Entity shootingEntity;

    public EntityPumpkinProjectile(World worldIn) {
        super(worldIn);
        rotation = rand.nextInt(20000);
        xTile = -1;
        yTile = -1;
        zTile = -1;
        setSize(1, 1);
        power = 1;
    }

    public EntityPumpkinProjectile(World worldIn, double x, double y, double z) {
        this(worldIn);
        setPosition(x, y, z);
    }

    public EntityPumpkinProjectile(World worldIn, EntityLivingBase shootingEntity, int power, int bounces, boolean isFiery, boolean canDestroyBlocks, @Nullable NBTTagCompound fireworkCompound) {
        this(worldIn, shootingEntity.posX, shootingEntity.posY + (double)shootingEntity.getEyeHeight() - 0.1D, shootingEntity.posZ);
        this.shootingEntity = shootingEntity;
        this.power = power;
        this.canDestroyBlocks = canDestroyBlocks;
        setIsSmoking(power > 0);
        setIsFlaming(isFiery);
        setBouncesLeft(bounces);
        if (fireworkCompound != null) {
            this.lifetimeRemaining = 6 * (fireworkCompound.getByte("Flight") + 1) + this.rand.nextInt(5);
            setFireworkNBT(fireworkCompound);
            setIsFirework(true);
        }
    }

    @Override
    protected void entityInit() {
        dataManager.register(BOUNCES_LEFT, 0);
        dataManager.register(IS_FLAMING, false);
        dataManager.register(IS_SMOKING, false);
        dataManager.register(IS_FIREWORK, false);
        dataManager.register(FIREWORK_NBT, new NBTTagCompound());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double value = getEntityBoundingBox().getAverageEdgeLength() * 8;

        if (Double.isNaN(value)) {
            value = 1;
        }

        value = value * 64 * getRenderDistanceWeight();
        return distance < value * value;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x + rand.nextGaussian() * 0.0075 * (double) inaccuracy;
        y = y + rand.nextGaussian() * 0.0075 * (double) inaccuracy;
        z = z + rand.nextGaussian() * 0.0075 * (double) inaccuracy;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;
        motionX = x;
        motionY = y;
        motionZ = z;
    }

    public void shoot(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy) {
        float x = - MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float y = - MathHelper.sin(pitch * 0.017453292F);
        float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        shoot(x, y, z, velocity, inaccuracy);
        motionX += shooter.motionX;
        motionZ += shooter.motionZ;

        if (!shooter.onGround) {
            motionY += shooter.motionY;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        rotation++;

        if (isFirework() && !world.isRemote) {
            if (lifetimeRemaining <= 0) {
                detonate();
            } else {
                lifetimeRemaining -= 1;
            }
        }

        Vec3d vecPrev = new Vec3d(posX, posY, posZ);
        Vec3d vecNew = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult rayTraceResult = world.rayTraceBlocks(vecPrev, vecNew, false, true, false);
        vecPrev = new Vec3d(posX, posY, posZ);
        vecNew = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

        if (rayTraceResult != null) {
            vecNew = new Vec3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
        }

        Entity hitEntity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1));
        double hitEntityDistance = 0.0D;
        boolean flag = false;

        for (Entity entity: list) {
            if (entity.canBeCollidedWith()) {
                if (entity == ignoreEntity) {
                    flag = true;
                } else if (shootingEntity != null && ticksExisted < 2 && ignoreEntity == null) {
                    ignoreEntity = entity;
                    flag = true;
                } else {
                    flag = false;
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(0.3D);
                    RayTraceResult rayTraceResult1 = axisalignedbb.calculateIntercept(vecPrev, vecNew);

                    if (rayTraceResult1 != null) {
                        double distance = vecPrev.squareDistanceTo(rayTraceResult1.hitVec);

                        if (distance < hitEntityDistance || hitEntityDistance == 0) {
                            hitEntity = entity;
                            hitEntityDistance = distance;
                        }
                    }
                }
            }
        }

        if (ignoreEntity != null) {
            if (flag) {
                ignoreTime = 2;
            } else if (this.ignoreTime-- <= 0) {
                ignoreEntity = null;
            }
        }

        if (hitEntity != null) {
            rayTraceResult = new RayTraceResult(hitEntity);
        }

        if (rayTraceResult != null) {
            if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(rayTraceResult.getBlockPos()).getBlock() == Blocks.PORTAL) {
                setPortal(rayTraceResult.getBlockPos());
            } else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
                onImpact(rayTraceResult);
            }
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        spawnParticles();

        double speedMultiplier = getSpeedMultiplier();
        motionX *= speedMultiplier;
        motionZ *= speedMultiplier;
        motionY = motionY * speedMultiplier - getGravityVelocity();

        setPosition(posX, posY, posZ);
        doBlockCollisions();
    }

    private void spawnParticles() {
        if (world.isRemote) {
            if (isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX, motionY, motionZ);
                }
            } else {
                if (isFlaming()) {
                    this.world.spawnParticle(EnumParticleTypes.FLAME, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.6, motionY * 0.6, motionZ * 0.6);
                }
                if (isSmoking()) {
                    for (int i = 0; i < 3; i++) {
                        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                    }
                    if (ticksExisted % 2 == 0) {
                        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                    }
                }
                if (isFirework()) {
                    world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, posX, posY - 0.3D, posZ, rand.nextGaussian() * 0.05D, - motionY * 0.5D, rand.nextGaussian() * 0.05D);
                }
            }
        }
    }

    private double getGravityVelocity() {
        if (hasNoGravity() || isFirework()) {
            return 0;
        }
        return 0.08;
    }

    private double getSpeedMultiplier() {
        if (isFirework()) {
            return 1;
        } else if (isInWater()) {
            return 0.9;
        }
        return 0.99;
    }

    protected void onImpact(RayTraceResult raytraceresult) {
        if (!world.isRemote) {
            if (isFlaming() && raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY && raytraceresult.entityHit instanceof EntityLiving) {
                raytraceresult.entityHit.setFire(power + 3);
            }
            if (getBouncesLeft() > 0 && !isInWater()) {
                if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (isFlaming() && world.isAirBlock(raytraceresult.getBlockPos().offset(raytraceresult.sideHit)) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)) {
                        world.setBlockState(raytraceresult.getBlockPos().offset(raytraceresult.sideHit), Blocks.FIRE.getDefaultState(), 11);
                    }
                }
            } else {
                detonate();
            }
        }

        if (getBouncesLeft() > 0 && !isInWater()) {
            setBouncesLeft(getBouncesLeft() - 1);
            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (raytraceresult.sideHit.getAxis() == EnumFacing.Axis.X) {
                    motionX = - motionX * 0.75;
                } else if (raytraceresult.sideHit.getAxis() == EnumFacing.Axis.Y) {
                    motionY = - motionY * 0.75;
                } else if (raytraceresult.sideHit.getAxis() == EnumFacing.Axis.Z) {
                    motionZ = - motionZ * 0.75;
                }
            } else if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY) {
                detonate();
            }
        }
    }

    private void detonate() {
        if (!world.isRemote) {
            boolean canMobGrief = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity);
            if (power > 0) {
                world.newExplosion(null, posX, posY, posZ, power + 1, canMobGrief && isFlaming(), canMobGrief && canDestroyBlocks);
            }
            if (isFirework()) {
                dealExplosionDamage();
                world.setEntityState(this, (byte) 17);
            }
            setDead();
        }
    }

    private void dealExplosionDamage() {
        int damageMultiplier = 0;
        NBTTagList tagList = getFireworkNBT().getTagList("Explosions", 10);

        if (!tagList.hasNoTags()) {
            damageMultiplier = 5 + tagList.tagCount() * 2;
        }

        if (damageMultiplier > 0) {
            Vec3d posVec = new Vec3d(posX, posY, posZ);

            for (EntityLivingBase entitylivingbase : world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(5.0D))) {
                if (getDistanceSq(entitylivingbase) <= 25) {
                    boolean flag = false;

                    for (int i = 0; i < 2; ++i) {
                        RayTraceResult raytraceresult = world.rayTraceBlocks(posVec, new Vec3d(entitylivingbase.posX, entitylivingbase.posY + entitylivingbase.height * 0.5D * i, entitylivingbase.posZ), false, true, false);

                        if (raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        entitylivingbase.attackEntityFrom(DamageSource.FIREWORKS, damageMultiplier * (float)Math.sqrt((5 - getDistance(entitylivingbase)) / 5));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 17 && world.isRemote) {
            world.makeFireworks(posX, posY, posZ, motionX, motionY, motionZ, getFireworkNBT());
        } else if (id == 103) {
            for (int j = 0; j < 16; ++j) {
                float f = rand.nextFloat() * ((float) Math.PI * 2F);
                float f1 = rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * f1;
                float f3 = MathHelper.cos(f) * f1;
                double x = posX + (double) f2;
                double z = posZ + (double) f3;
                world.spawnParticle(EnumParticleTypes.SLIME, x, getEntityBoundingBox().minY, z, 0, 0, 0);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public int getBouncesLeft() {
        return dataManager.get(BOUNCES_LEFT);
    }

    private void setBouncesLeft(int bounces) {
        dataManager.set(BOUNCES_LEFT, bounces);
    }

    public boolean isFlaming() {
        return dataManager.get(IS_FLAMING);
    }

    private void setIsFlaming(boolean isFlaming) {
        dataManager.set(IS_FLAMING, isFlaming);
    }

    public boolean isSmoking() {
        return dataManager.get(IS_SMOKING);
    }

    private void setIsSmoking(boolean isSmoking) {
        dataManager.set(IS_SMOKING, isSmoking);
    }

    public boolean isFirework() {
        return dataManager.get(IS_FIREWORK);
    }
    private void setIsFirework(boolean isFirework) {
        dataManager.set(IS_FIREWORK, isFirework);
    }

    public NBTTagCompound getFireworkNBT() {
        return dataManager.get(FIREWORK_NBT);
    }

    private void setFireworkNBT(NBTBase compound) {
        dataManager.set(FIREWORK_NBT, (NBTTagCompound) compound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setByte("power", (byte) this.power);
        compound.setBoolean("canDestroyBlocks", this.canDestroyBlocks);
        compound.setByte("bouncesLeft", (byte) getBouncesLeft());
        compound.setBoolean("isFiery", isFlaming());
        compound.setBoolean("isSmoking", isSmoking());
        compound.setBoolean("isFirework", isFirework());
        compound.setTag("fireworkTag", getFireworkNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.power = compound.getByte("power");
        this.canDestroyBlocks = compound.getBoolean("canDestroyBlocks");
        setBouncesLeft(compound.getByte("bouncesLeft"));
        setIsFlaming(compound.getBoolean("isFiery"));
        setIsSmoking(compound.getBoolean("isSmoking"));
        setIsFirework(compound.getBoolean("isFirework"));
        setFireworkNBT(compound.getTag("fireworkTag"));
    }
}
