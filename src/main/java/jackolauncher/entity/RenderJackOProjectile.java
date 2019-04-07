package jackolauncher.entity;

import jackolauncher.JackOLauncher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class RenderJackOProjectile extends Render<EntityJackOProjectile> {

    private static final ResourceLocation TEXTURES = new ResourceLocation(JackOLauncher.MODID, "textures/jack_o_projectile.png");
    private static final ModelBase MODEL = new ModelPumpkinProjectile();

    public RenderJackOProjectile(RenderManager rendermanager) {
        super(rendermanager);
        shadowSize = 0.5F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(EntityJackOProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.isInvisible()) {
            float rotation = entity.ticksInAir + entity.randomRotationOffset + partialTicks;
            GlStateManager.pushMatrix();
            bindEntityTexture(entity);
            GlStateManager.translatef((float) x, (float) y, (float) z);
            MODEL.render(entity, 0, rotation * 20, 0, 0, 0, 0.0625F);
            GlStateManager.popMatrix();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(EntityJackOProjectile entity) {
        return TEXTURES;
    }

    private static class ModelPumpkinProjectile extends ModelBase {

        private final ModelRenderer cube;

        ModelPumpkinProjectile() {
            textureWidth = 64;
            textureHeight = 32;
            cube = new ModelRenderer(this, 0, 0);
            cube.addBox(-8, -8, -8, 16, 16, 16, 0);
        }

        @Override
        public void render(@Nullable Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(limbSwingAmount / 2.5F, 0.7071F, 0, 0.7071F);
            GlStateManager.rotatef(limbSwingAmount, 0, 1, 0);
            cube.render(scale);
            GlStateManager.popMatrix();
        }
    }
}
