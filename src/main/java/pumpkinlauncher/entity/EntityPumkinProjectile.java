package pumpkinlauncher.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
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
public class EntityPumkinProjectile extends Entity implements IProjectile {

    private int xTile;
    private int yTile;
    private int zTile;
    private int ignoreTime;
    private Entity ignoreEntity;
    private int power;
    private boolean isFiery;
    private boolean canDestroyBlocks;
    private int bouncesLeft;
    private boolean shouldSendPackets = true;
    /* rendering */
    int rotation;
    private boolean shouldSpawnSmokeParticles;

    @Nullable
    private Entity shootingEntity;

    public EntityPumkinProjectile(World worldIn) {
        super(worldIn);
        this.rotation = this.rand.nextInt(20000);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.setSize(1.0F, 1.0F);
        this.power = 1;
    }

    public EntityPumkinProjectile(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public EntityPumkinProjectile(World worldIn, EntityLivingBase shootingEntity, int power, int bounces, boolean isFiery, boolean canDestroyBlocks) {
        this(worldIn, shootingEntity.posX, shootingEntity.posY + (double)shootingEntity.getEyeHeight() - 0.1D, shootingEntity.posZ);
        this.shootingEntity = shootingEntity;
        this.power = power;
        this.isFiery = isFiery;
        this.canDestroyBlocks = canDestroyBlocks;
        this.bouncesLeft = bounces;
    }

    @Override
    protected void entityInit() { }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 8.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.0075D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.0075D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.0075D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public void shoot(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float f1 = -MathHelper.sin(pitch * 0.017453292F);
        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += shooter.motionX;
        this.motionZ += shooter.motionZ;

        if (!shooter.onGround) {
            this.motionY += shooter.motionY;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (180D / Math.PI));
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (shouldSendPackets) {
            shouldSendPackets = false;
            if (isFiery) {
                world.setEntityState(this, (byte) 102);
            } else if (power > 0) {
                world.setEntityState(this, (byte) 101);
            }
        }

        rotation++;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
        vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (raytraceresult != null) {
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        Entity hitEntity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        double d0 = 0.0D;
        boolean flag = false;

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                if (entity == this.ignoreEntity) {
                    flag = true;
                } else if (this.shootingEntity != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
                    this.ignoreEntity = entity;
                    flag = true;
                } else {
                    flag = false;
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(0.3D);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                    if (raytraceresult1 != null) {
                        double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                        if (d1 < d0 || d0 == 0.0D) {
                            hitEntity = entity;
                            d0 = d1;
                        }
                    }
                }
            }
        }

        if (this.ignoreEntity != null) {
            if (flag) {
                this.ignoreTime = 2;
            } else if (this.ignoreTime-- <= 0) {
                this.ignoreEntity = null;
            }
        }

        if (hitEntity != null) {
            raytraceresult = new RayTraceResult(hitEntity);
        }

        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
                this.setPortal(raytraceresult.getBlockPos());
            } else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onImpact(raytraceresult);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) { this.prevRotationPitch -= 360.0F; }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) { this.prevRotationPitch += 360.0F; }
        while (this.rotationYaw - this.prevRotationYaw < -180.0F) { this.prevRotationYaw -= 360.0F; }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) { this.prevRotationYaw += 360.0F; }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        spawnParticles();

        float speedMultiplier = 0.99F;
        float gravityVelocity = this.getGravityVelocity();

        if (this.isInWater()) {
            speedMultiplier = 0.9F;
        }

        this.motionX *= (double) speedMultiplier;
        this.motionY *= (double) speedMultiplier;
        this.motionZ *= (double) speedMultiplier;

        if (!this.hasNoGravity()) {
            this.motionY -= (double) gravityVelocity;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.doBlockCollisions();
    }

    private void spawnParticles() {
        if (isInWater()) {
            if (isFiery) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
            }
            for (int j = 0; j < 4; ++j) {
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX, motionY, motionZ);
            }
        } else {
            if (isFiery) {
                this.world.spawnParticle(EnumParticleTypes.FLAME, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.6, motionY * 0.6, motionZ * 0.6);
            }
            if (shouldSpawnSmokeParticles) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                if (ticksExisted % 2 == 0) {
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX - motionX * 0.25D + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                }
            }
        }
    }

    protected float getGravityVelocity() {
        return 0.08F;
    }

    protected void onImpact(RayTraceResult raytraceresult) {
        if (!this.world.isRemote) {
            if (this.isFiery && raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY && raytraceresult.entityHit instanceof EntityLiving) {
                raytraceresult.entityHit.setFire(this.power + 3);
            }
            if (bouncesLeft > 0 && !isInWater()) {
                bouncesLeft--;
                world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (raytraceresult.sideHit.getAxis() == EnumFacing.Axis.X) {
                        this.motionX = -this.motionX;
                    } else if (raytraceresult.sideHit.getAxis() == EnumFacing.Axis.Y) {
                        this.motionY = -this.motionY;
                    } else if (raytraceresult.sideHit.getAxis() == EnumFacing.Axis.Z) {
                        this.motionZ = -this.motionZ;
                    }
                    if (isFiery && world.isAirBlock(raytraceresult.getBlockPos().offset(raytraceresult.sideHit)) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
                        world.setBlockState(raytraceresult.getBlockPos().offset(raytraceresult.sideHit), Blocks.FIRE.getDefaultState(), 11);
                    }
                } else if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY) {
                    this.motionX = -this.motionX;
                    this.motionY = -this.motionY;
                    this.motionZ = -this.motionZ;
                }
                this.motionX *= 0.75;
                this.motionY *= 0.75;
                this.motionZ *= 0.75;
            } else {
                boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
                this.world.newExplosion(null, this.posX, this.posY, this.posZ, power + 1, flag && isFiery, flag && canDestroyBlocks);
                this.setDead();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 101) {
            this.shouldSpawnSmokeParticles = true;
        } else if (id == 102) {
            this.isFiery = true;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setByte("power", (byte) this.power);
        compound.setBoolean("isFiery", this.isFiery);
        compound.setBoolean("canDestroyBlocks", this.canDestroyBlocks);
        compound.setByte("bouncesLeft", (byte) this.bouncesLeft);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.power = compound.getByte("power");
        this.isFiery = compound.getBoolean("isFiery");
        this.canDestroyBlocks = compound.getBoolean("canDestroyBlocks");
        this.bouncesLeft = compound.getByte("bouncesLeft");
    }
}
