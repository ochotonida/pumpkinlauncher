package jackolauncher.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class RenderJackOProjectile extends Render<EntityJackOProjectile> {

    public RenderJackOProjectile(RenderManager rendermanager) {
        super(rendermanager);
        shadowSize = 0.5F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(EntityJackOProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translatef((float) x, (float) y + 0.5F, (float) z);
        float rotation = 20 * (entity.ticksInAir + entity.randomRotationOffset + partialTicks);
        GlStateManager.rotatef(rotation / 2.5F, 0.7071F, 0, 0.7071F);
        GlStateManager.rotatef(rotation, 0, 1, 0);
        GlStateManager.translatef(-0.5F, -0.5F, +0.5F);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightness(blockrendererdispatcher.getModelForState(Blocks.JACK_O_LANTERN.getDefaultState()), Blocks.JACK_O_LANTERN.getDefaultState(), 1, false);

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(EntityJackOProjectile entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
