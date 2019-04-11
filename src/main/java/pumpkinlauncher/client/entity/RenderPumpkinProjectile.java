package pumpkinlauncher.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.common.entity.EntityPumpkinProjectile;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
public class RenderPumpkinProjectile extends Render<EntityPumpkinProjectile> {

    public static final Factory FACTORY = new Factory();

    private RenderPumpkinProjectile(RenderManager rendermanager) {
        super(rendermanager);
        shadowSize = 0.5F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(EntityPumpkinProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);
        float rotation = 20 * (entity.rotation + partialTicks);
        GlStateManager.rotate(rotation / 2.5F, 0.7071F, 0, 0.7071F);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.translate(-0.5F, -0.5F, +0.5F);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightness(blockrendererdispatcher.getModelForState(Blocks.LIT_PUMPKIN.getDefaultState()), Blocks.LIT_PUMPKIN.getDefaultState(), 1, false);

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(EntityPumpkinProjectile entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityPumpkinProjectile> {

        @Override
        public Render<? super EntityPumpkinProjectile> createRenderFor(RenderManager manager) {
            return new RenderPumpkinProjectile(manager);
        }
    }
}
