package pumpkinlauncher.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public enum Particles {
    PUMPKIN_BREAK(new ParticlePumpkin.Factory());

    IParticleFactory factory;

    Particles(IParticleFactory factory) {
        this.factory = factory;
    }

    protected Particle create(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        return factory.createParticle(-1, world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    public void spawn(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        if (!world.isRemote) {
            return;
        }
        Particle particle = create(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
        spawn(particle);
    }

    private void spawn(Particle particle) {
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }
}
