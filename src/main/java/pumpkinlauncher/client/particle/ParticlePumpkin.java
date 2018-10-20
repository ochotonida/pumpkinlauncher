package pumpkinlauncher.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class ParticlePumpkin extends Particle {

    private ParticlePumpkin(World worldIn, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        super(worldIn, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        setParticleTexture(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.PUMPKIN.getDefaultState()));
        particleGravity = Blocks.STONE.blockParticleGravity;
        particleRed = 0.6F;
        particleGreen = 0.6F;
        particleBlue = 0.6F;
        particleScale /= 2.0F;
    }

    public int getFXLayer() {
        return 1;
    }

    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f = (particleTextureIndexX + particleTextureJitterX / 4) / 16;
        float f1 = f + 0.015609375F;
        float f2 = (particleTextureIndexY + particleTextureJitterY / 4) / 16;
        float f3 = f2 + 0.015609375F;
        float f4 = 0.1F * particleScale;

        if (particleTexture != null)
        {
            f = particleTexture.getInterpolatedU(particleTextureJitterX / 4 * 16);
            f1 = particleTexture.getInterpolatedU((particleTextureJitterX + 1) / 4 * 16);
            f2 = particleTexture.getInterpolatedV(particleTextureJitterY / 4 * 16);
            f3 = particleTexture.getInterpolatedV((particleTextureJitterY + 1) / 4 * 16);
        }

        float f5 = (float)(prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float f6 = (float)(prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float f7 = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
        int i = getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        buffer.pos(f5 - rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 - rotationYZ * f4 - rotationXZ * f4).tex(f, f3).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
        buffer.pos(f5 - rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 - rotationYZ * f4 + rotationXZ * f4).tex(f, f2).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
        buffer.pos(f5 + rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 + rotationYZ * f4 + rotationXZ * f4).tex(f1, f2).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
        buffer.pos(f5 + rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 + rotationYZ * f4 - rotationXZ * f4).tex(f1, f3).color(particleRed, particleGreen, particleBlue, 1).lightmap(j, k).endVertex();
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
            return new ParticlePumpkin(world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
