package jackolauncher.entity;

import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.*;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityJackOProjectile extends Entity implements IProjectile {

    private static final DataParameter<Integer> BOUNCES_LEFT = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_FLAMING = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SMOKING = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_ENDER_PEARL = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_BONE_MEAL = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<NBTTagCompound> FIREWORKS_NBT = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<ItemStack> POTION_STACK = EntityDataManager.createKey(EntityJackOProjectile.class, DataSerializers.ITEM_STACK);
    protected int ticksInAir = 0;
    protected int randomRotationOffset;
    protected boolean shouldHurtShooter = true;
    private int explosionPower = 2;
    private int extraDamage = 0;
    private int fireworkLifetime = 0;
    private boolean canDestroyBlocks = true;
    private ItemStack arrowStack = ItemStack.EMPTY;
    private @Nullable
    EntityLivingBase shootingEntity;

    public EntityJackOProjectile(World world) {
        super(JackOLauncher.JACK_O_PROJECTILE_ENTITY_TYPE, world);
        randomRotationOffset = rand.nextInt(1000);
        setSize(1F, 1F);
    }

    private EntityJackOProjectile(World worldIn, double x, double y, double z, NBTTagCompound ammoNBT) {
        this(worldIn);
        setPosition(x, y, z);

        if (ammoNBT.hasKey("ExtraDamage")) {
            extraDamage = ammoNBT.getByte("ExtraDamage");
        }
        if (ammoNBT.hasKey("ExplosionPower")) {
            explosionPower = ammoNBT.getByte("ExplosionPower");
        }
        if (ammoNBT.hasKey("BouncesAmount")) {
            dataManager.set(BOUNCES_LEFT, (int) ammoNBT.getByte("BouncesAmount"));
        }
        if (ammoNBT.hasKey("IsFlaming")) {
            dataManager.set(IS_FLAMING, ammoNBT.getBoolean("IsFlaming"));
        }
        if (ammoNBT.hasKey("HasBoneMeal")) {
            dataManager.set(HAS_BONE_MEAL, ammoNBT.getBoolean("HasBoneMeal"));
        }
        if (ammoNBT.hasKey("IsEnderPearl")) {
            dataManager.set(IS_ENDER_PEARL, ammoNBT.getBoolean("IsEnderPearl"));
        }
        if (ammoNBT.hasKey("CanDestroyBlocks")) {
            canDestroyBlocks = ammoNBT.getBoolean("CanDestroyBlocks");
        }
        if (ammoNBT.hasKey("ArrowsNBT")) {
            arrowStack = ItemStack.read(ammoNBT.getCompound("ArrowsNBT"));
        }
        if (ammoNBT.hasKey("PotionNBT")) {
            dataManager.set(POTION_STACK, ItemStack.read(ammoNBT.getCompound("PotionNBT")));
        }
        if (ammoNBT.hasKey("FireworksNBT")) {
            NBTTagCompound fireworkCompound = ammoNBT.getCompound("FireworksNBT");
            fireworkLifetime = 6 * (fireworkCompound.getByte("Flight") + 1) + rand.nextInt(5);
            dataManager.set(FIREWORKS_NBT, fireworkCompound);
        }

        dataManager.set(IS_SMOKING, explosionPower > 0);
    }

    public EntityJackOProjectile(World worldIn, EntityLivingBase shootingEntity, NBTTagCompound ammoNBT, boolean shouldHurtShooter) {
        this(worldIn, shootingEntity.posX, shootingEntity.posY + (double) shootingEntity.getEyeHeight() - 0.1, shootingEntity.posZ, ammoNBT);
        this.shootingEntity = shootingEntity;
        this.shouldHurtShooter = shouldHurtShooter;
    }

    @Override
    protected void registerData() {
        dataManager.register(BOUNCES_LEFT, 1);
        dataManager.register(IS_FLAMING, false);
        dataManager.register(IS_SMOKING, false);
        dataManager.register(HAS_BONE_MEAL, false);
        dataManager.register(FIREWORKS_NBT, new NBTTagCompound());
        dataManager.register(POTION_STACK, ItemStack.EMPTY);
        dataManager.register(IS_ENDER_PEARL, false);
    }

    @Override
    protected void readAdditional(NBTTagCompound compound) {
        this.explosionPower = compound.getByte("ExplosionPower");
        this.canDestroyBlocks = compound.getBoolean("CanDestroyBlocks");
        dataManager.set(BOUNCES_LEFT, (int) compound.getByte("BouncesLeft"));
        dataManager.set(IS_FLAMING, compound.getBoolean("IsFiery"));
        dataManager.set(IS_SMOKING, compound.getBoolean("IsSmoking"));
        dataManager.set(IS_ENDER_PEARL, compound.getBoolean("IsEnderPearl"));
        dataManager.set(HAS_BONE_MEAL, compound.getBoolean("HasBoneMeal"));
        dataManager.set(FIREWORKS_NBT, compound.getCompound("FireworksNBT"));
        ticksInAir = compound.getInt("TicksInAir");
        fireworkLifetime = compound.getInt("FireworkLifeTime");
        if (compound.hasKey("ArrowNBT") && !compound.getCompound("ArrowsNBT").isEmpty()) {
            arrowStack = ItemStack.read(compound.getCompound("ArrowsNBT"));
        }
        if (compound.hasKey("PotionNBT") && !compound.getCompound("PotionNBT").isEmpty()) {
            dataManager.set(POTION_STACK, ItemStack.read(compound.getCompound("PotionNBT")));
        }
    }

    @Override
    protected void writeAdditional(NBTTagCompound compound) {
        compound.setByte("ExplosionPower", (byte) explosionPower);
        compound.setBoolean("CanDestroyBlocks", canDestroyBlocks);
        compound.setByte("BouncesLeft", dataManager.get(BOUNCES_LEFT).byteValue());
        compound.setBoolean("IsFiery", dataManager.get(IS_FLAMING));
        compound.setBoolean("IsSmoking", dataManager.get(IS_SMOKING));
        compound.setBoolean("IsEnderPearl", dataManager.get(IS_ENDER_PEARL));
        compound.setBoolean("HasBoneMeal", dataManager.get(HAS_BONE_MEAL));
        compound.setTag("FireworksNBT", dataManager.get(FIREWORKS_NBT));
        compound.setInt("TicksInAir", ticksInAir);
        compound.setInt("FireworkLifetime", fireworkLifetime);
        if (!arrowStack.isEmpty()) {
            compound.setTag("ArrowsNBT", arrowStack.write(new NBTTagCompound()));
        }
        if (!dataManager.get(POTION_STACK).isEmpty()) {
            compound.setTag("PotionNBT", dataManager.get(POTION_STACK).write(new NBTTagCompound()));
        }
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        motionX = x * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        motionY = y * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        motionZ = z * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
    }

    public void shoot(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy) {
        float x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float y = -MathHelper.sin(pitch * 0.017453292F);
        float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        shoot(x, y, z, velocity, inaccuracy);
        motionX += shooter.motionX;
        motionZ += shooter.motionZ;

        if (!shooter.onGround) {
            motionY += shooter.motionY;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (dataManager.get(IS_ENDER_PEARL) && shootingEntity != null) {
            if (!shootingEntity.isAlive()) {
                dataManager.set(IS_ENDER_PEARL, false);
            }
        }

        if (!dataManager.get(FIREWORKS_NBT).isEmpty() && !world.isRemote) {
            if (ticksInAir == 0) {
                world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.NEUTRAL, 2, 1);
            }
            if (ticksInAir > fireworkLifetime) {
                detonate(null);
            }
        }

        ++ticksInAir;
        spawnParticles();

        RayTraceResult rayTraceResult = ProjectileHelper.forwardsRaycast(this, true, ticksInAir >= 25, shootingEntity);
        //noinspection ConstantConditions
        if (rayTraceResult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
            onImpact(rayTraceResult);
        }

        if (dataManager.get(FIREWORKS_NBT).isEmpty()) {
            motionY -= 0.08;
            double motionMultiplier = isInWater() ? 0.9 : 0.99;
            motionX *= motionMultiplier;
            motionY *= motionMultiplier;
            motionZ *= motionMultiplier;
        }
        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        setPosition(posX, posY, posZ);

        doBlockCollisions();
    }

    private void onImpact(RayTraceResult rayTraceResult) {
        if (!world.isRemote) {
            if (rayTraceResult.type == RayTraceResult.Type.ENTITY && rayTraceResult.entity instanceof EntityLiving) {
                if (shootingEntity instanceof EntityPlayer && rayTraceResult.entity != shootingEntity) {
                    rayTraceResult.entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) shootingEntity), 1 + 2 * extraDamage);
                } else {
                    rayTraceResult.entity.attackEntityFrom(DamageSource.GENERIC, 1 + 2 * extraDamage);
                }
                if (dataManager.get(IS_FLAMING)) {
                    rayTraceResult.entity.setFire(4);
                }
            }
            if (rayTraceResult.type == RayTraceResult.Type.BLOCK) {
                if (dataManager.get(IS_FLAMING) && world.isAirBlock(rayTraceResult.getBlockPos().offset(rayTraceResult.sideHit)) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)) {
                    world.setBlockState(rayTraceResult.getBlockPos().offset(rayTraceResult.sideHit), Blocks.FIRE.getDefaultState(), 11);
                }
            }
            if (dataManager.get(BOUNCES_LEFT) <= 0 || isInWater()) {
                detonate(rayTraceResult);
                return;
            }
        }
        if (dataManager.get(BOUNCES_LEFT) > 0 && !isInWater()) {
            bounce(rayTraceResult);
        }
    }

    private void bounce(RayTraceResult rayTraceResult) {
        dataManager.set(BOUNCES_LEFT, dataManager.get(BOUNCES_LEFT) - 1);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        if (rayTraceResult.type == RayTraceResult.Type.BLOCK) {
            if (rayTraceResult.sideHit.getAxis() == EnumFacing.Axis.X) {
                motionX = -motionX * 0.75;
            } else if (rayTraceResult.sideHit.getAxis() == EnumFacing.Axis.Y) {
                motionY = -motionY * 0.75;
            } else if (rayTraceResult.sideHit.getAxis() == EnumFacing.Axis.Z) {
                motionZ = -motionZ * 0.75;
            }
            world.setEntityState(this, (byte) 100);
            if (dataManager.get(HAS_BONE_MEAL) && !world.isRemote) {
                // noinspection deprecation
                if (ItemBoneMeal.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, rayTraceResult.getBlockPos())) {
                    world.playEvent(2005, rayTraceResult.getBlockPos(), 0);
                }
            }
        } else if (rayTraceResult.type == RayTraceResult.Type.ENTITY) {
            detonate(rayTraceResult);
        }
    }

    private void detonate(@Nullable RayTraceResult result) {
        if (!world.isRemote) {
            if (dataManager.get(IS_ENDER_PEARL)) {
                doEnderPearlThings(result);
            }
            if (arrowStack != null && !arrowStack.isEmpty()) {
                spawnArrows(result);
            }

            boolean canMobGrief = shootingEntity == null || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity);
            if (explosionPower > 0) {
                new CustomExplosion(world, this, shootingEntity, posX, posY, posZ, (explosionPower + 2) / 2.25F, extraDamage, canMobGrief && dataManager.get(IS_FLAMING), canMobGrief && canDestroyBlocks, shouldHurtShooter).detonate();
            } else {
                world.setEntityState(this, (byte) 101);
                world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }

            if (dataManager.get(HAS_BONE_MEAL)) {
                doBoneMealThings();
            }

            if (!dataManager.get(POTION_STACK).isEmpty() && (dataManager.get(POTION_STACK).getItem() == Items.SPLASH_POTION || dataManager.get(POTION_STACK).getItem() == Items.LINGERING_POTION)) {
                doPotionThings(result);
            }
            if (!dataManager.get(FIREWORKS_NBT).isEmpty()) {
                dealFireworkDamage();
                world.setEntityState(this, (byte) 17);
            }
            remove();
        }
    }

    private void doEnderPearlThings(@Nullable RayTraceResult result) {
        if (result != null && result.entity != null) {
            if (result.entity == shootingEntity) {
                return;
            }
            result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, shootingEntity), 0.0F);
        }

        for (int i = 0; i < 32; ++i) {
            world.spawnParticle(Particles.PORTAL, posX, posY + rand.nextDouble() * 2.0D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
        }

        if (!this.world.isRemote && shootingEntity != null) {
            teleportEntity(shootingEntity, posX, posY, posZ);
        }
    }

    private void teleportEntity(EntityLivingBase entity, double posX, double posY, double posZ) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entity;

            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == world && !entityplayermp.isPlayerSleeping()) {
                entity.stopRiding();
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
                arrow.shootingEntity = shootingEntity.getUniqueID();
            }
            double x = motionX;
            double y = motionY;
            double z = motionZ;
            if (result != null && result.type == RayTraceResult.Type.BLOCK) {
                if (result.sideHit.getAxis() == EnumFacing.Axis.X) {
                    x = -motionX;
                } else if (result.sideHit.getAxis() == EnumFacing.Axis.Y) {
                    y = -motionY;
                } else if (result.sideHit.getAxis() == EnumFacing.Axis.Z) {
                    z = -motionZ;
                }
            } else if (result != null && result.type == RayTraceResult.Type.ENTITY) {
                x = rand.nextDouble() * 2 - 1;
                y = rand.nextDouble();
                z = rand.nextDouble() * 2 - 1;
            }
            arrow.shoot(x, y, z, MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ), 10);
            world.spawnEntity(arrow);
        }
    }

    private void doBoneMealThings() {
        for (BlockPos pos : BlockPos.getAllInBox((int) (posX + 0.5) - 5, (int) (posY + 0.5) - 5, (int) (posZ + 0.5) - 5, (int) (posX + 0.5) + 5, (int) (posY + 0.5) + 5, (int) (posZ + 0.5) + 5)) {
            // noinspection deprecation
            if (rand.nextInt(8) == 0 && ItemBoneMeal.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos)) {
                world.playEvent(2005, pos, 0);
            }
        }
    }

    private void doPotionThings(@Nullable RayTraceResult result) {
        if (!world.isRemote) {
            ItemStack stack = dataManager.get(POTION_STACK);
            PotionType type = PotionUtils.getPotionFromItem(stack);
            List<PotionEffect> list = PotionUtils.getEffectsFromStack(stack);
            boolean isWater = type == PotionTypes.WATER && list.isEmpty();

            if (result != null && result.type == RayTraceResult.Type.BLOCK && isWater) {
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

    private void extinguishFires(BlockPos pos, EnumFacing face) {
        if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            this.world.extinguishFire(null, pos.offset(face), face.getOpposite());
        }
    }

    private void applyWater() {
        AxisAlignedBB axisalignedbb = getBoundingBox().grow(5, 3, 5);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, EntityPotion.WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for (EntityLivingBase entitylivingbase : list) {
                double distance = getDistanceSq(entitylivingbase);

                if (distance < 16.0D && (entitylivingbase instanceof EntityEnderman || entitylivingbase instanceof EntityBlaze)) {
                    entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1);
                }
            }
        }
    }

    private void applySplash(@Nullable RayTraceResult result, List<PotionEffect> effects) {
        AxisAlignedBB boundingBox = getBoundingBox().grow(5, 3, 5);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox);

        if (!list.isEmpty()) {
            for (EntityLivingBase entity : list) {
                if (entity.canBeHitWithPotion()) {
                    double distance = getDistanceSq(entity);

                    if (distance < 16) {
                        double effectMultiplier = 1 - Math.sqrt(distance) / 6;

                        if (result != null && entity == result.entity) {
                            effectMultiplier = 1;
                        }

                        for (PotionEffect potioneffect : effects) {
                            Potion potion = potioneffect.getPotion();

                            if (potion.isInstant()) {
                                potion.affectEntity(this, shootingEntity, entity, potioneffect.getAmplifier(), effectMultiplier);
                            } else {
                                int duration = (int) (effectMultiplier * (double) potioneffect.getDuration() + 0.5);

                                if (duration > 20) {
                                    entity.addPotionEffect(new PotionEffect(potion, duration, potioneffect.getAmplifier(), potioneffect.isAmbient(), potioneffect.doesShowParticles()));
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
        entityareaeffectcloud.setRadius(3.2F);
        entityareaeffectcloud.setRadiusOnUse(-0.4F);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / entityareaeffectcloud.getDuration());
        entityareaeffectcloud.setPotion(type);

        for (PotionEffect potioneffect : PotionUtils.getFullEffectsFromItem(stack)) {
            entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
        }

        NBTTagCompound nbttagcompound = stack.getTag();
        if (nbttagcompound != null && nbttagcompound.contains("CustomPotionColor", 99)) {
            entityareaeffectcloud.setColor(nbttagcompound.getInt("CustomPotionColor"));
        }

        world.spawnEntity(entityareaeffectcloud);
    }

    private void dealFireworkDamage() {
        int damageMultiplier = 0;
        NBTTagList tagList = dataManager.get(FIREWORKS_NBT).getList("Explosions", 10);

        if (!tagList.isEmpty()) {
            damageMultiplier = 5 + tagList.size() * 2;
        }

        if (damageMultiplier > 0) {
            Vec3d posVec = new Vec3d(posX, posY, posZ);

            for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(5))) {
                if (getDistanceSq(entity) <= 25) {
                    boolean flag = false;

                    for (int i = 0; i < 2; ++i) {
                        RayTraceResult raytraceresult = world.rayTraceBlocks(posVec, new Vec3d(entity.posX, entity.posY + entity.height * 0.5 * i, entity.posZ), RayTraceFluidMode.NEVER, true, false);

                        if (raytraceresult == null || raytraceresult.type == RayTraceResult.Type.MISS) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        entity.attackEntityFrom(DamageSource.FIREWORKS, damageMultiplier * (float) Math.sqrt((5 - getDistance(entity)) / 5));
                    }
                }
            }
        }
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType dimension, net.minecraftforge.common.util.ITeleporter teleporter) {
        if (shootingEntity != null && shootingEntity.dimension != dimension) {
            dataManager.set(IS_ENDER_PEARL, false);
        }

        return super.changeDimension(dimension, teleporter);
    }

    private void spawnParticles() {
        if (world.isRemote) {
            if (isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    world.spawnParticle(Particles.BUBBLE, posX - motionX * 0.25, posY - motionY * 0.25, posZ - motionZ * 0.25, motionX, motionY, motionZ);
                }
            } else {
                if (dataManager.get(IS_FLAMING)) {
                    world.spawnParticle(Particles.FLAME, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25D + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25 + rand.nextDouble() * 0.5 - 0.25, motionX * 0.6, motionY * 0.6, motionZ * 0.6);
                }
                if (dataManager.get(IS_SMOKING)) {
                    for (int i = 0; i < 3; i++) {
                        world.spawnParticle(Particles.SMOKE, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25 + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25 + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                    }
                    if (ticksExisted % 2 == 0) {
                        world.spawnParticle(Particles.LARGE_SMOKE, posX - motionX * 0.25 + rand.nextDouble() * 0.5 - 0.25, posY - motionY * 0.25 + rand.nextDouble() * 0.5 - 0.25, posZ - motionZ * 0.25 + rand.nextDouble() * 0.5 - 0.25, motionX * 0.3, motionY * 0.3, motionZ * 0.3);
                    }
                }
                if (!dataManager.get(FIREWORKS_NBT).isEmpty()) {
                    world.spawnParticle(Particles.FIREWORK, posX, posY - 0.3, posZ, rand.nextGaussian() * 0.05, -motionY * 0.5, rand.nextGaussian() * 0.05);
                }
                if (dataManager.get(HAS_BONE_MEAL) && ticksExisted % 3 == 0) {
                    world.spawnParticle(Particles.HAPPY_VILLAGER, posX + rand.nextDouble() * 0.5, posY + rand.nextDouble() * 0.5, posZ + rand.nextDouble() * 0.5, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02);
                }
                if (dataManager.get(IS_ENDER_PEARL)) {
                    world.spawnParticle(Particles.PORTAL, posX + rand.nextDouble() * 0.5, posY + rand.nextDouble() * 0.5, posZ + rand.nextDouble() * 0.5, rand.nextGaussian() * 0.08, rand.nextGaussian() * 0.08, rand.nextGaussian() * 0.08);
                }
                if (!dataManager.get(POTION_STACK).isEmpty()) {
                    int color = PotionUtils.getColor(dataManager.get(POTION_STACK));
                    if (color > 0) { // TODO fix this
                        world.spawnParticle(Particles.EFFECT, posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, (color >> 16 & 255) / 255D, (color >> 8 & 255) / 255D, (color & 255) / 255D);
                    }
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        switch (id) {
            case (17):
                world.makeFireworks(posX, posY, posZ, motionX, motionY, motionZ, dataManager.get(FIREWORKS_NBT));
                break;
            case (100):
                for (int j = 0; j < 16; ++j) {
                    float rotationXZ = (float) (rand.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (rand.nextFloat() * Math.PI);
                    float distance = rand.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    world.spawnParticle(Particles.ITEM_SLIME, posX + x, posY + y, posZ + z, 0, 0, 0);
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
                    world.spawnParticle(new BlockParticleData(Particles.BLOCK, Blocks.PUMPKIN.getDefaultState()), posX + x, posY + y, posZ + z, 0, 0, 0);

                }
                break;
            default:
                super.handleStatusUpdate(id);
                break;
        }
    }
}
