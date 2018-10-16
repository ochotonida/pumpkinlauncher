package pumpkinlauncher.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.client.ParticlePumpkin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static net.minecraft.entity.projectile.EntityPotion.WATER_SENSITIVE;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class EntityPumpkinProjectile extends Entity implements IProjectile {

    private static final DataParameter<Integer> BOUNCES_LEFT = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_FLAMING = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SMOKING = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<NBTTagCompound> FIREWORK_NBT = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<ItemStack> POTION_ITEM = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.ITEM_STACK);

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
    private EntityLivingBase shootingEntity;

    public EntityPumpkinProjectile(World worldIn) {
        super(worldIn);
        rotation = rand.nextInt(20000);
        xTile = -1;
        yTile = -1;
        zTile = -1;
        setSize(1F, 1F);
        power = 1;
    }

    public EntityPumpkinProjectile(World worldIn, double x, double y, double z) {
        this(worldIn);
        setPosition(x, y, z);
    }

    public EntityPumpkinProjectile(World worldIn, EntityLivingBase shootingEntity, int power, int bounces, boolean isFiery, boolean canDestroyBlocks, @Nullable NBTTagCompound fireworkCompound, @Nullable ItemStack potionItem) {
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
        }
        if (potionItem != null && !potionItem.isEmpty()) {
            setPotion(potionItem);
        }
    }

    @Override
    protected void entityInit() {
        dataManager.register(BOUNCES_LEFT, 0);
        dataManager.register(IS_FLAMING, false);
        dataManager.register(IS_SMOKING, false);
        dataManager.register(FIREWORK_NBT, new NBTTagCompound());
        dataManager.register(POTION_ITEM, ItemStack.EMPTY);
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

        if (isFirework() && !world.isRemote) {
            if (lifetimeRemaining <= 0) {
                detonate(null);
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

        double speedMultiplier = getSpeedMultiplier();
        motionX *= speedMultiplier;
        motionZ *= speedMultiplier;
        motionY = motionY * speedMultiplier - getGravityVelocity();

        setPosition(posX, posY, posZ);
        doBlockCollisions();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        rotation++;
        spawnParticles();
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
                if (!getPotion().isEmpty()) {
                    int color = PotionUtils.getColor(getPotion());
                    if (color > 0) {
                        double r = (color >> 16 & 255) / 255D;
                        double g = (color >> 8 & 255) / 255D;
                        double b = (color & 255) / 255D;
                        world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, r, g, b);
                    }
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
            if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY && raytraceresult.entityHit instanceof EntityLiving) {
                if (shootingEntity instanceof EntityPlayer) {
                    raytraceresult.entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) shootingEntity), 1);
                }
                if (isFlaming()) {
                    raytraceresult.entityHit.setFire(power + 3);
                }
            }
            if (getBouncesLeft() > 0 && !isInWater()) {
                if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (isFlaming() && world.isAirBlock(raytraceresult.getBlockPos().offset(raytraceresult.sideHit)) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)) {
                        world.setBlockState(raytraceresult.getBlockPos().offset(raytraceresult.sideHit), Blocks.FIRE.getDefaultState(), 11);
                    }
                }
            } else {
                detonate(raytraceresult);
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
                world.setEntityState(this, (byte) 100);
            } else if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY) {
                detonate(raytraceresult);
            }
        }
    }

    private void detonate(@Nullable RayTraceResult result) {
        if (!world.isRemote) {
            boolean canMobGrief = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity);
            if (power > 0) {
                CustomExplosion.createExplosion(world, this, shootingEntity, posX, posY, posZ, power + 1, canMobGrief && isFlaming(), canMobGrief && canDestroyBlocks);
            } else {
                // explode in particles
                world.setEntityState(this, (byte) 101);
            }
            if (!getPotion().isEmpty() && (getPotion().getItem() == Items.SPLASH_POTION || getPotion().getItem() == Items.LINGERING_POTION)) {
                doPotionThings(result);
            }
            if (isFirework()) {
                dealFireworkDamage();
                // explode fireworks
                world.setEntityState(this, (byte) 17);
            }
            setDead();
        }
        if (power <= 0) {
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
    }

    private void doPotionThings(@Nullable RayTraceResult result) {
        if (!world.isRemote) {
            ItemStack stack = this.getPotion();
            PotionType type = PotionUtils.getPotionFromItem(stack);
            List<PotionEffect> list = PotionUtils.getEffectsFromStack(stack);
            boolean isWater = type == PotionTypes.WATER && list.isEmpty();

            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && isWater) {
                BlockPos pos = result.getBlockPos().offset(result.sideHit);
                this.extinguishFires(pos, result.sideHit);

                for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
                    this.extinguishFires(pos.offset(face), face);
                }
            }

            if (isWater) {
                this.applyWater();
            } else if (!list.isEmpty()) {
                if (stack.getItem() == Items.LINGERING_POTION) {
                    this.makeAreaOfEffectCloud(stack, type);
                } else {
                    this.applySplash(result, list);
                }
            }

            int eventType = type.hasInstantEffect() ? 2007 : 2002;
            this.world.playEvent(eventType, new BlockPos(this), PotionUtils.getColor(stack));
        }
    }

    private void extinguishFires(BlockPos pos, EnumFacing face) {
        if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            this.world.extinguishFire(null, pos.offset(face), face.getOpposite());
        }
    }

    private void applyWater() {
        AxisAlignedBB axisalignedbb = getEntityBoundingBox().grow(6, 3, 6);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, WATER_SENSITIVE);

        if (!list.isEmpty()) {
            for (EntityLivingBase entitylivingbase : list) {
                double distance = getDistanceSq(entitylivingbase);

                if (distance < 16.0D && (entitylivingbase instanceof EntityEnderman || entitylivingbase instanceof EntityBlaze)) {
                    entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1.0F);
                }
            }
        }
    }

    private void applySplash(@Nullable RayTraceResult result, List<PotionEffect> effects) {
        AxisAlignedBB boundingBox = getEntityBoundingBox().grow(5, 3, 5);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox);

        if (!list.isEmpty()) {
            for (EntityLivingBase entity : list) {
                if (entity.canBeHitWithPotion()) {
                    double distance = getDistanceSq(entity);

                    if (distance < 16) {
                        double effectMultiplier = 1 - Math.sqrt(distance) / 6;

                        if (result != null && entity == result.entityHit) {
                            effectMultiplier = 1;
                        }

                        for (PotionEffect potioneffect : effects) {
                            Potion potion = potioneffect.getPotion();

                            if (potion.isInstant()) {
                                potion.affectEntity(this, shootingEntity, entity, potioneffect.getAmplifier(), effectMultiplier);
                            } else {
                                int duration = (int)(effectMultiplier * (double)potioneffect.getDuration() + 0.5D);

                                if (duration > 20) {
                                    entity.addPotionEffect(new PotionEffect(potion, duration, potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void makeAreaOfEffectCloud(ItemStack stack, PotionType type) {
        EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(world, posX, posY, posZ);
        entityareaeffectcloud.setOwner(shootingEntity);
        entityareaeffectcloud.setRadius(4F);
        entityareaeffectcloud.setRadiusOnUse(-0.5F);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / entityareaeffectcloud.getDuration());
        entityareaeffectcloud.setPotion(type);

        for (PotionEffect potioneffect : PotionUtils.getFullEffectsFromItem(stack)) {
            entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
        }

        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99)) {
            entityareaeffectcloud.setColor(nbttagcompound.getInteger("CustomPotionColor"));
        }

        this.world.spawnEntity(entityareaeffectcloud);
    }

    private void dealFireworkDamage() {
        int damageMultiplier = 0;
        NBTTagList tagList = getFireworkNBT().getTagList("Explosions", 10);

        if (!tagList.hasNoTags()) {
            damageMultiplier = 5 + tagList.tagCount() * 2;
        }

        if (damageMultiplier > 0) {
            Vec3d posVec = new Vec3d(posX, posY, posZ);

            for (EntityLivingBase entitylivingbase : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(5.0D))) {
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
        switch (id) {
            case (17):
                world.makeFireworks(posX, posY, posZ, motionX, motionY, motionZ, getFireworkNBT());
                break;
            case (100):
                for (int j = 0; j < 16; ++j) {
                    float rotationXZ = (float) (rand.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (rand.nextFloat() * Math.PI);
                    float distance = rand.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    world.spawnParticle(EnumParticleTypes.SLIME, posX + x, posY + y, posZ + z, 0, 0, 0);
                }
                break;
            case (101):
                for (int j = 0; j < 48; ++j) {
                    float rotationXZ = (float) (rand.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (rand.nextFloat() * Math.PI);
                    float distance = rand.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    Particle particle = new ParticlePumpkin(world, posX + x, posY + y, posZ + z, 0, 0, 0);
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                }
                break;
            default:
                super.handleStatusUpdate(id);
                break;
        }
    }

    public int getBouncesLeft() {
        return dataManager.get(BOUNCES_LEFT);
    }

    private void setBouncesLeft(int bounces) {
        dataManager.set(BOUNCES_LEFT, bounces);
        dataManager.setDirty(BOUNCES_LEFT);
    }

    public boolean isFlaming() {
        return dataManager.get(IS_FLAMING);
    }

    private void setIsFlaming(boolean isFlaming) {
        dataManager.set(IS_FLAMING, isFlaming);
        dataManager.setDirty(IS_FLAMING);
    }

    public boolean isSmoking() {
        return dataManager.get(IS_SMOKING);
    }

    private void setIsSmoking(boolean isSmoking) {
        dataManager.set(IS_SMOKING, isSmoking);
        dataManager.setDirty(IS_SMOKING);
    }

    public @Nonnull NBTTagCompound getFireworkNBT() {
        return dataManager.get(FIREWORK_NBT);
    }

    private void setFireworkNBT(NBTBase compound) {
        dataManager.set(FIREWORK_NBT, (NBTTagCompound) compound);
        dataManager.setDirty(FIREWORK_NBT);
    }

    public boolean isFirework() {
        return !dataManager.get(FIREWORK_NBT).hasNoTags();
    }

    public @Nonnull ItemStack getPotion() {
        return dataManager.get(POTION_ITEM);
    }

    private void setPotion(ItemStack itemstack) {
        dataManager.set(POTION_ITEM, itemstack);
        dataManager.setDirty(POTION_ITEM);
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
        compound.setTag("fireworkTag", getFireworkNBT());
        if (!getPotion().isEmpty()) {
            compound.setTag("potionTag", getPotion().writeToNBT(new NBTTagCompound()));
        }
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
        setFireworkNBT(compound.getTag("fireworkTag"));
        if (compound.hasKey("potionTag") && !compound.getTag("potionTag").hasNoTags()) {
            setPotion(new ItemStack(compound.getCompoundTag("potionTag")));
        }
    }

    private static class CustomExplosion extends Explosion {

        private final EntityLivingBase shootingEntity;

        private CustomExplosion(World world, Entity explodingEntity, @Nullable EntityLivingBase shootingEntity, double x, double y, double z, float power, boolean isFlaming, boolean isSmoking) {
            super (world, explodingEntity, x, y, z, power, isFlaming, isSmoking);
            this.shootingEntity = shootingEntity;
        }

        public static void createExplosion(World world, Entity explodingEntity, @Nullable EntityLivingBase shootingEntity, double x, double y, double z, float power, boolean isFlaming, boolean isSmoking) {
            CustomExplosion explosion = new CustomExplosion(world, explodingEntity, shootingEntity, x, y, z, power, isFlaming, isSmoking);
            if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return;
            explosion.doExplosionA();
            if (world instanceof WorldServer) {
                explosion.doExplosionB(false);
                if (!isSmoking) {
                    explosion.clearAffectedBlockPositions();
                }
                for (EntityPlayer entityplayer : world.playerEntities) {
                    if (entityplayer.getDistanceSq(x, y, z) < 4096.0D) {
                        ((EntityPlayerMP) entityplayer).connection.sendPacket(new SPacketExplosion(x, y, z, power, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityplayer)));
                    }
                }
            } else {
                explosion.doExplosionB(true);
            }
        }

        @Override
        @Nullable
        public EntityLivingBase getExplosivePlacedBy() {
            return shootingEntity != null ? shootingEntity : super.getExplosivePlacedBy();
        }
    }
}
