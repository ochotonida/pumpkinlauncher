package jackolauncher.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JackOProjectileRenderer extends EntityRenderer<JackOProjectileEntity> {

    public JackOProjectileRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        shadowSize = 0.5F;
    }

    @Override
    public void render(JackOProjectileEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
        /*GlStateManager.pushMatrix();

        GlStateManager.translatef((float) x, (float) y + 0.5F, (float) z);
        float rotation = 20 * (entity.ticksInAir + entity.randomRotationOffset + partialTicks);
        GlStateManager.rotatef(rotation / 2.5F, 0.7071F, 0, 0.7071F);
        GlStateManager.rotatef(rotation, 0, 1, 0);
        GlStateManager.translatef(-0.5F, -0.5F, +0.5F);

        bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);fa
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        blockrendererdispatcher.getBlockModelRenderer()(blockrendererdispatcher.getModelForState(entity.getBlockState()), entity.getBlockState(), 1, false);

        GlStateManager.popMatrix();*/
        BlockState state = entity.getBlockState();
        if (state.getRenderType() == BlockRenderType.MODEL) {
            World world = entity.world;
            if (state != world.getBlockState(new BlockPos(entity)) && state.getRenderType() != BlockRenderType.INVISIBLE) {
                stack.push();
                BlockPos blockpos = new BlockPos(entity.getPosX(), entity.getBoundingBox().maxY, entity.getPosZ());
                stack.translate(-0.5D, 0.0D, -0.5D);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
                for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.getBlockRenderTypes()) {
                    if (RenderTypeLookup.canRenderInLayer(state, type)) {
                        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
                        // noinspection deprecation
                        blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(state), state, blockpos, stack, buffer.getBuffer(type), false, new Random(), 0, OverlayTexture.NO_OVERLAY);
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
                stack.pop();
                super.render(entity, entityYaw, partialTicks, stack, buffer, light);
            }
        }
    }

    @Override
    public ResourceLocation getEntityTexture(JackOProjectileEntity entity) {
        // noinspection deprecation
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
