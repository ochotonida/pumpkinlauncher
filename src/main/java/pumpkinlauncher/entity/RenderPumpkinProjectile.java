package pumpkinlauncher.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.PumpkinLauncher;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
public class RenderPumpkinProjectile extends Render<EntityPumpkinProjectile> {

    public static final Factory FACTORY = new Factory();
    private static final ResourceLocation TEXTURES = new ResourceLocation(PumpkinLauncher.MODID, "textures/entity/pumpkinprojectile/pumpkinprojectile.png");
    private final ModelBase MODEL = new ModelPumpkinProjectile();

    private RenderPumpkinProjectile(RenderManager rendermanager) {
        super(rendermanager);
        this.shadowSize = 0.5F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(EntityPumpkinProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
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
    protected ResourceLocation getEntityTexture(EntityPumpkinProjectile entity) {
        return TEXTURES;
    }

    public static class Factory implements IRenderFactory<EntityPumpkinProjectile> {

        @Override
        public Render<? super EntityPumpkinProjectile> createRenderFor(RenderManager manager) {
            return new RenderPumpkinProjectile(manager);
        }
    }

    private static class ModelPumpkinProjectile extends ModelBase {
        private ModelRenderer cube;

        ModelPumpkinProjectile() {
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.cube = new ModelRenderer(this, 0, 0);
            this.cube.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.cube.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F);
        }

        @Override
        public void render(@Nullable Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.pushMatrix();

            GlStateManager.rotate(limbSwingAmount / 2.5F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();
        }

        @Override
        public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
            this.cube.rotateAngleX = entityIn.rotationPitch;
        }
    }
}
