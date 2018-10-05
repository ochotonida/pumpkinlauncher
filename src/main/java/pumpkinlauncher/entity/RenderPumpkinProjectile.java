package pumpkinlauncher.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.PumpkinLauncher;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
public class RenderPumpkinProjectile extends Render<EntityPumkinProjectile> {

    public static final Factory FACTORY = new Factory();
    private static final ResourceLocation TEXTURES = new ResourceLocation(PumpkinLauncher.MODID, "textures/entity/pumpkinprojectile/pumpkinprojectile.png");
    private final ModelBase MODEL = new ModelPumpkinProjectile();

    private RenderPumpkinProjectile(RenderManager rendermanager) {
        super(rendermanager);
        this.shadowSize = 0.5F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(EntityPumkinProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float f = entity.rotation + partialTicks;
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        this.MODEL.render(entity, 0.0F, f * 20.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(EntityPumkinProjectile entity) {
        return TEXTURES;
    }

    public static class Factory implements IRenderFactory<EntityPumkinProjectile> {

        @Override
        public Render<? super EntityPumkinProjectile> createRenderFor(RenderManager manager) {
            return new RenderPumpkinProjectile(manager);
        }
    }
}
