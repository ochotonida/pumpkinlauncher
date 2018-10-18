package pumpkinlauncher.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pumpkinlauncher.common.item.ItemPumpkinLauncher;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {

    /**
     * Changes the armpose of the player to BOW_AND_ARROW while holding the launcher
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntityLiving(RenderLivingEvent.Pre<EntityPlayer> event){
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            boolean isHoldingOffhand = player.getHeldItemOffhand().getItem() instanceof ItemPumpkinLauncher;
            boolean isHoldingMainhand = player.getHeldItemMainhand().getItem() instanceof ItemPumpkinLauncher;
            if ((isHoldingMainhand && Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.RIGHT) || (isHoldingOffhand && Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.LEFT)) {
                ((ModelBiped) event.getRenderer().getMainModel()).rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            } else if ((isHoldingMainhand && Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.LEFT) || (isHoldingOffhand && Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.RIGHT)) {
                ((ModelBiped) event.getRenderer().getMainModel()).leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            }
        }
    }
}
