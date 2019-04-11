package pumpkinlauncher.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.client.particle.Particles;
import pumpkinlauncher.common.explosion.CustomExplosion;

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
    private static final DataParameter<Boolean> IS_ENDER_PEARL = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_BONE_MEAL = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<NBTTagCompound> FIREWORK_NBT = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<ItemStack> POTION_ITEM = EntityDataManager.createKey(EntityPumpkinProjectile.class, DataSerializers.ITEM_STACK);
    public int rotation;
    @Nullable
    private EntityLivingBase shootingEntity;
    private int power = 2;
    private int ignoreTime;
    private int extraDamage;
    private int fireworkLifetime;
    private int fireworkLifetimeMax = -1;
    private boolean canDestroyBlocks = true;
    private boolean shouldHurtPlayer;
    private Entity ignoreEntity;
    private ItemStack arrowStack = ItemStack.EMPTY;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    public EntityPumpkinProjectile(World worldIn) {
        super(worldIn);
        rotation = rand.nextInt(200);
        setSize(0.8F, 0.8F);
    }

    public EntityPumpkinProjectile(World worldIn, double x, double y, double z, ItemStack ammoStack) {
        this(worldIn);
        setPosition(x, y, z);
        if (ammoStack.getTagCompound() != null) {
            readEntityFromNBT(ammoStack.getTagCompound());
        }
    }

    public EntityPumpkinProjectile(World worldIn, EntityLivingBase shootingEntity, ItemStack ammoStack, boolean shouldHurtPlayer) {
        this(worldIn, shootingEntity.posX, shootingEntity.posY + (double) shootingEntity.getEyeHeight() - 0.1, shootingEntity.posZ, ammoStack);
        this.shootingEntity = shootingEntity;
        this.shouldHurtPlayer = shouldHurtPlayer;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Dataparameters
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void entityInit() {
        dataManager.register(BOUNCES_LEFT, 0);
        dataManager.register(IS_FLAMING, false);
        dataManager.register(IS_SMOKING, true);
        dataManager.register(HAS_BONE_MEAL, false);
        dataManager.register(FIREWORK_NBT, new NBTTagCompound());
        dataManager.register(POTION_ITEM, ItemStack.EMPTY);
        dataManager.register(IS_ENDER_PEARL, false);
    }

    private void setBouncesLeft(int bounces) {
        dataManager.set(BOUNCES_LEFT, bounces);
        dataManager.setDirty(BOUNCES_LEFT);
    }

    private void setIsFlaming(boolean isFlaming) {
        dataManager.set(IS_FLAMING, isFlaming);
        dataManager.setDirty(IS_FLAMING);
    }

    private void setIsSmoking(boolean isSmoking) {
        dataManager.set(IS_SMOKING, isSmoking);
        dataManager.setDirty(IS_SMOKING);
    }

    private void setHasBoneMeal(boolean hasBoneMeal) {
        dataManager.set(HAS_BONE_MEAL, hasBoneMeal);
        dataManager.setDirty(HAS_BONE_MEAL);
    }

    private void setFireworkNBT(NBTTagCompound compound) {
        dataManager.set(FIREWORK_NBT, compound);
        fireworkLifetimeMax = 6 * (compound.getByte("Flight") + 1) + rand.nextInt(5);
        dataManager.setDirty(FIREWORK_NBT);
    }

    private void setPotion(ItemStack itemstack) {
        dataManager.set(POTION_ITEM, itemstack);
        dataManager.setDirty(POTION_ITEM);
    }

    private void setIsEnderPearl(boolean isEnderPearl) {
        dataManager.set(IS_ENDER_PEARL, isEnderPearl);
        dataManager.setDirty(IS_ENDER_PEARL);
    }

    ///////////////////////////////////////////////////////////////////////////
    // IProjectile && launcher shoot implementation
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        motionX = x * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        motionY = y * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        motionZ = z * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
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

    ///////////////////////////////////////////////////////////////////////////
    // update loop
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Update the entity's position and logic
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        rotation++;
        spawnParticles();

        if (dataManager.get(IS_ENDER_PEARL) && shootingEntity != null) {
            if (!shootingEntity.isEntityAlive()) {
                setIsEnderPearl(false);
            }
        }

        if (!dataManager.get(FIREWORK_NBT).hasNoTags() && !world.isRemote) {
            if (fireworkLifetime == 0) {
                world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            }
            if (fireworkLifetime > fireworkLifetimeMax) {
                detonate(null);
            } else {
                fireworkLifetime += 1;
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
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1));
        list.remove(shootingEntity);

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

        double speedMultiplier = isInWater() ? 0.9 : 1;
        motionX *= speedMultiplier;
        motionZ *= speedMultiplier;
        motionY = motionY * speedMultiplier - (hasNoGravity() || !dataManager.get(FIREWORK_NBT).hasNoTags() ? 0 : 0.08);

        setPosition(posX, posY, posZ);
        doBlockCollisions();
    }

    /**
     * Called every tick to spawn particles depending on the projectile's properties
     */
    private void spawnParticles() {
        if (world.isRemote) {
            if (isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25D + rand.nextDouble() * 0.5 - 0.25, motionX, motionY, motionZ);
                }
            } else {
                if (dataManager.get(IS_FLAMING)) {
                    world.spawnParticle(EnumParticleTypes.FLAME, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25 + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25 + rand.nextDouble() * 0.5 - 0.25, motionX * 0.6, motionY * 0.6, motionZ * 0.6);
                }
                if (dataManager.get(IS_SMOKING)) {
                    for (int i = 0; i < 3; i++) {
                        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25 + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25 + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                    }
                    if (ticksExisted % 2 == 0) {
                        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25 + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25 + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                    }
                }
                if (!dataManager.get(FIREWORK_NBT).hasNoTags()) {
                    world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, posX, posY - 0.3, posZ, rand.nextGaussian() * 0.05, -motionY * 0.5, rand.nextGaussian() * 0.05);
                }
                if (dataManager.get(HAS_BONE_MEAL) && ticksExisted % 3 == 0) {
                    world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, posX + rand.nextDouble() * 0.5, posY + rand.nextDouble() * 0.5, posZ + rand.nextDouble() * 0.5, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02);
                }
                if (dataManager.get(IS_ENDER_PEARL)) {
                    world.spawnParticle(EnumParticleTypes.PORTAL, posX + rand.nextDouble() * 0.5, posY + rand.nextDouble() * 0.5, posZ + rand.nextDouble() * 0.5, rand.nextGaussian() * 0.08, rand.nextGaussian() * 0.08, rand.nextGaussian() * 0.08);
                }
                if (!dataManager.get(POTION_ITEM).isEmpty()) {
                    int color = PotionUtils.getColor(dataManager.get(POTION_ITEM));
                    if (color > 0) {
                        world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, (color >> 16 & 255) / 255D, (color >> 8 & 255) / 255D, (color & 255) / 255D);
                    }
                }
            }
        }
    }

    protected void onImpact(RayTraceResult raytraceresult) {
        if (!world.isRemote) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY && raytraceresult.entityHit instanceof EntityLiving) {
                if (shootingEntity instanceof EntityPlayer && raytraceresult.entityHit != shootingEntity) {
                    raytraceresult.entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) shootingEntity), 1 + 2 * extraDamage);
                } else {
                    raytraceresult.entityHit.attackEntityFrom(DamageSource.GENERIC, 1 + 2 * extraDamage);
                }
                if (dataManager.get(IS_FLAMING)) {
                    raytraceresult.entityHit.setFire(4);
                }
            }
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (dataManager.get(IS_FLAMING) && world.isAirBlock(raytraceresult.getBlockPos().offset(raytraceresult.sideHit)) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)) {
                    world.setBlockState(raytraceresult.getBlockPos().offset(raytraceresult.sideHit), Blocks.FIRE.getDefaultState(), 11);
                }
            }
            if (dataManager.get(BOUNCES_LEFT) <= 0 || isInWater()) {
                detonate(raytraceresult);
                return;
            }
        }
        if (dataManager.get(BOUNCES_LEFT) > 0 && !isInWater()) {
            bounce(raytraceresult);
        }
    }

    // slime ball impact
    private void bounce(RayTraceResult result) {
        setBouncesLeft(dataManager.get(BOUNCES_LEFT) - 1);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1, 1);
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (result.sideHit.getAxis() == EnumFacing.Axis.X) {
                motionX = - motionX * 0.75;
            } else if (result.sideHit.getAxis() == EnumFacing.Axis.Y) {
                motionY = - motionY * 0.75;
            } else if (result.sideHit.getAxis() == EnumFacing.Axis.Z) {
                motionZ = - motionZ * 0.75;
            }
            world.setEntityState(this, (byte) 100);
            if (dataManager.get(HAS_BONE_MEAL) && !world.isRemote) {
                if (ItemDye.applyBonemeal(new ItemStack(Items.DYE, EnumDyeColor.WHITE.getDyeDamage()), world, result.getBlockPos())) {
                    world.playEvent(2005, result.getBlockPos(), 0);
                }
            }
        } else if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            detonate(result);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // detonation
    ///////////////////////////////////////////////////////////////////////////

    private void detonate(@Nullable RayTraceResult result) {
        if (!world.isRemote) {
            if (dataManager.get(IS_ENDER_PEARL)) {
                doEnderPearlThings(result);
            }
            if (arrowStack != null && !arrowStack.isEmpty()) {
                spawnArrows(result);
            }
            boolean canMobGrief = shootingEntity == null || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity);
            if (power > 0) {
                new CustomExplosion(world, this, shootingEntity, posX, posY, posZ, (power + 2) / 2.25F, extraDamage, canMobGrief && dataManager.get(IS_FLAMING), canMobGrief && canDestroyBlocks, shouldHurtPlayer).detonate();
            } else {
                world.setEntityState(this, (byte) 101);
            }
            if (dataManager.get(HAS_BONE_MEAL)) {
                doBoneMealThings();
            }
            ItemStack potionStack = dataManager.get(POTION_ITEM);
            if (!potionStack.isEmpty() && (potionStack.getItem() == Items.SPLASH_POTION || potionStack.getItem() == Items.LINGERING_POTION)) {
                doPotionThings(result);
            }
            if (!dataManager.get(FIREWORK_NBT).hasNoTags()) {
                dealFireworkDamage();
                world.setEntityState(this, (byte) 17);
            }
            setDead();
        }
        if (power <= 0) {
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
    }

    // ender pearl detonate
    private void doEnderPearlThings(@Nullable RayTraceResult result) {
        if (result != null && result.entityHit != null) {
            if (result.entityHit == shootingEntity) {
                return;
            }
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, shootingEntity), 0);
        }

        for (int i = 0; i < 32; ++i) {
            world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY + rand.nextDouble() * 2, posZ, rand.nextGaussian(), 0, rand.nextGaussian());
        }

        if (!this.world.isRemote && shootingEntity != null) {
            teleportEntity(shootingEntity, posX, posY, posZ);
        }
    }

    // ender pearl detonate teleport
    private void teleportEntity(EntityLivingBase entity, double posX, double posY, double posZ) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entity;

            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == world && !entityplayermp.isPlayerSleeping()) {
                if (entity.isRiding()) {
                    entity.dismountRidingEntity();
                }

                entity.setPositionAndUpdate(posX, posY, posZ);
                entity.fallDistance = 0;
                entity.attackEntityFrom(DamageSource.FALL, 3);
            }
        } else {
            entity.setPositionAndUpdate(posX, posY, posZ);
            entity.fallDistance = 0;
        }
    }

    private void spawnArrows(@Nullable RayTraceResult result) {
        for (int i = 0; i < arrowStack.getCount(); i++) {
            EntityArrow arrow;
            if (shootingEntity != null) {
                arrow = ((ItemArrow) arrowStack.getItem()).createArrow(world, arrowStack, shootingEntity);
            } else {
                if (!(world instanceof WorldServer)) {
                    return;
                }
                arrow = ((ItemArrow) arrowStack.getItem()).createArrow(world, arrowStack, FakePlayerFactory.getMinecraft((WorldServer) world));
            }
            arrow.posX = posX;
            arrow.posY = posY;
            arrow.posZ = posZ;
            arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
            arrow.setDamage(arrow.getDamage() * 2.5);
            if (shootingEntity != null) {
                arrow.shootingEntity = shootingEntity;
            }
            double x = motionX;
            double y = motionY;
            double z = motionZ;
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (result.sideHit.getAxis() == EnumFacing.Axis.X) {
                    x = - motionX;
                } else if (result.sideHit.getAxis() == EnumFacing.Axis.Y) {
                    y = - motionY;
                } else if (result.sideHit.getAxis() == EnumFacing.Axis.Z) {
                    z = - motionZ;
                }
            } else if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
                x = rand.nextDouble() * 2 - 1;
                y = rand.nextDouble();
                z = rand.nextDouble() * 2 - 1;
            }
            arrow.shoot(x, y, z, MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ), 10);
            world.spawnEntity(arrow);
        }
    }

    // bone meal detonate
    private void doBoneMealThings() {
        for (BlockPos pos : BlockPos.getAllInBox((int) (posX + 0.5) - 5, (int) (posY + 0.5) - 5, (int) (posZ + 0.5) - 5, (int) (posX + 0.5) + 5, (int) (posY + 0.5) + 5, (int) (posZ + 0.5) + 5)) {
            if (rand.nextInt(8) == 0 && ItemDye.applyBonemeal(new ItemStack(Items.DYE, EnumDyeColor.WHITE.getDyeDamage()), world, pos)) {
                world.playEvent(2005, pos, 0);
            }
        }
    }

    // potion effect detonate
    private void doPotionThings(@Nullable RayTraceResult result) {
        if (!world.isRemote) {
            ItemStack stack = dataManager.get(POTION_ITEM);
            PotionType type = PotionUtils.getPotionFromItem(stack);
            List<PotionEffect> list = PotionUtils.getEffectsFromStack(stack);
            boolean isWater = type == PotionTypes.WATER && list.isEmpty();

            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && isWater) {
                BlockPos pos = result.getBlockPos().offset(result.sideHit);
                extinguishFires(pos, result.sideHit);

                for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
                    extinguishFires(pos.offset(face), face);
                }
            }

            if (isWater) {
                applyWater();
            } else if (!list.isEmpty()) {
                if (stack.getItem() == Items.LINGERING_POTION) {
                    makeAreaOfEffectCloud(stack, type);
                } else {
                    applySplash(result, list);
                }
            }

            // spawn particles
            int eventType = type.hasInstantEffect() ? 2007 : 2002;
            world.playEvent(eventType, new BlockPos(this), PotionUtils.getColor(stack));
        }
    }

    // water potion block hit
    private void extinguishFires(BlockPos pos, EnumFacing face) {
        if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            world.extinguishFire(null, pos.offset(face), face.getOpposite());
        }
    }

    // water potion detonate
    private void applyWater() {
        AxisAlignedBB axisalignedbb = getEntityBoundingBox().grow(5, 3, 5);
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

    // splash potion detonate
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

    // lingering potion detonate
    private void makeAreaOfEffectCloud(ItemStack stack, PotionType type) {
        EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(world, posX, posY, posZ);
        entityareaeffectcloud.setOwner(shootingEntity);
        entityareaeffectcloud.setRadius(3.2F);
        entityareaeffectcloud.setRadiusOnUse(-0.4F);
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

    // firework (star) detonate
    private void dealFireworkDamage() {
        int damageMultiplier = 0;
        NBTTagList tagList = dataManager.get(FIREWORK_NBT).getTagList("Explosions", 10);

        if (!tagList.hasNoTags()) {
            damageMultiplier = 5 + tagList.tagCount() * 2;
        }

        if (damageMultiplier > 0) {
            Vec3d posVec = new Vec3d(posX, posY, posZ);

            for (EntityLivingBase entitylivingbase : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(5.0D))) {
                if (getDistanceSq(entitylivingbase) <= 25) {
                    boolean flag = false;

                    for (int i = 0; i < 2; ++i) {
                        RayTraceResult raytraceresult = world.rayTraceBlocks(posVec, new Vec3d(entitylivingbase.posX, entitylivingbase.posY + entitylivingbase.height * 0.5 * i, entitylivingbase.posZ), false, true, false);

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

    ///////////////////////////////////////////////////////////////////////////

    // prevent shooting entity from teleporting through portals
    public @Nullable Entity changeDimension(int dimensionIn, net.minecraftforge.common.util.ITeleporter teleporter) {
        if (shootingEntity != null && shootingEntity.dimension != dimensionIn) {
            setIsEnderPearl(false);
        }

        return super.changeDimension(dimensionIn, teleporter);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        switch (id) {
            case (17):
                world.makeFireworks(posX, posY, posZ, motionX, motionY, motionZ, dataManager.get(FIREWORK_NBT));
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
                    Particles.PUMPKIN_BREAK.spawn(world, posX + x, posY + y, posZ + z, 0, 0, 0);
                }
                break;
            default:
                super.handleStatusUpdate(id);
                break;
        }
    }

    // increase render distance range
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double value = getEntityBoundingBox().getAverageEdgeLength() * 12;

        if (Double.isNaN(value)) {
            value = 1;
        }

        value = value * 64 * getRenderDistanceWeight();
        return distance < value * value;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setByte("power", (byte) power);
        compound.setByte("extraDamage", (byte) extraDamage);
        compound.setBoolean("canDestroyBlocks", canDestroyBlocks);
        compound.setByte("bounceAmount", dataManager.get(BOUNCES_LEFT).byteValue());
        compound.setBoolean("isFiery", dataManager.get(IS_FLAMING));
        compound.setBoolean("isEnderPearl", dataManager.get(IS_ENDER_PEARL));
        compound.setBoolean("hasBonemeal", dataManager.get(HAS_BONE_MEAL));
        compound.setTag("fireworks", dataManager.get(FIREWORK_NBT));
        compound.setInteger("fireworkLifetime", fireworkLifetime);
        compound.setInteger("fireworkLifetimeMax", fireworkLifetimeMax);
        if (!arrowStack.isEmpty()) {
            compound.setTag("arrowTag", arrowStack.writeToNBT(new NBTTagCompound()));
        }
        ItemStack potionStack = dataManager.get(POTION_ITEM);
        if (!potionStack.isEmpty()) {
            compound.setTag("potionTag", potionStack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        extraDamage = compound.getByte("extraDamage");
        fireworkLifetime = compound.getInteger("fireworkLifetime");

        setBouncesLeft(compound.getByte("bounceAmount"));
        setIsFlaming(compound.getBoolean("isFiery"));
        setHasBoneMeal(compound.getBoolean("hasBonemeal"));
        setFireworkNBT(compound.getCompoundTag("fireworks"));
        setIsEnderPearl(compound.getBoolean("isEnderPearl"));

        if (compound.hasKey("power"))
            power = compound.getByte("power");
        if (compound.hasKey("canDestroyBlocks"))
            canDestroyBlocks = compound.getBoolean("canDestroyBlocks");
        if (compound.hasKey("potionTag"))
            setPotion(new ItemStack(compound.getCompoundTag("potionTag")));
        if (compound.hasKey("arrowTag"))
            arrowStack = new ItemStack(compound.getCompoundTag("arrowTag"));

        setIsSmoking(power > 0);
    }
}
