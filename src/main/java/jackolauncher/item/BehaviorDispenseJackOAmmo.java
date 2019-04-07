package jackolauncher.item;

import jackolauncher.entity.EntityJackOProjectile;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BehaviorDispenseJackOAmmo extends BehaviorProjectileDispense {

    @Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
        return new EntityJackOProjectile(world, position.getX(), position.getY(), position.getZ(), stack.getOrCreateChildTag("AmmoNBT"));
    }

    protected float getProjectileVelocity() {
        return 1.3F;
    }
}
