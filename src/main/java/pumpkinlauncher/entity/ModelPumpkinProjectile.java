package pumpkinlauncher.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public class ModelPumpkinProjectile extends ModelBase {

    private ModelRenderer cube;

    public ModelPumpkinProjectile() {
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
