package jackolauncher;

import jackolauncher.item.ItemJackOLauncher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JackOLauncher.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void setJackOLauncherArmPose(RenderLivingEvent.Pre event) {
        boolean isHoldingOffHand = event.getEntity().getHeldItemOffhand().getItem() instanceof ItemJackOLauncher;
        boolean isHoldingMainHand = event.getEntity().getHeldItemMainhand().getItem() instanceof ItemJackOLauncher;
        if ((isHoldingMainHand && Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT) || (isHoldingOffHand && Minecraft.getInstance().gameSettings.mainHand == HandSide.LEFT)) {
            ((BipedModel) event.getRenderer().getEntityModel()).rightArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
        } else if ((isHoldingMainHand && Minecraft.getInstance().gameSettings.mainHand == HandSide.LEFT) || (isHoldingOffHand && Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT)) {
            ((BipedModel) event.getRenderer().getEntityModel()).leftArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
        }
    }
}
