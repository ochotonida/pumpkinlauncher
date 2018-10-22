package pumpkinlauncher.common.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pumpkinlauncher.common.entity.EntityPumpkinProjectile;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BehaviorDispensePumpkinAmmo extends BehaviorProjectileDispense {

    @Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
        return new EntityPumpkinProjectile(world, position.getX(), position.getY(), position.getZ(), stack);
    }

    protected float getProjectileVelocity() {
        return 1.3F;
    }
}
