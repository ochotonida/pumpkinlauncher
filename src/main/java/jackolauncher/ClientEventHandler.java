package jackolauncher;

import jackolauncher.entity.EntityJackOProjectile;
import jackolauncher.entity.RenderJackOProjectile;
import jackolauncher.item.ItemJackOLauncher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JackOLauncher.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void registerJackOProjectileModel(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityJackOProjectile.class, RenderJackOProjectile::new);
    }

    @SubscribeEvent
    public static void setJackOLauncherArmPose(RenderLivingEvent.Pre<EntityPlayer> event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();
        boolean isHoldingOffHand = player.getHeldItemOffhand().getItem() instanceof ItemJackOLauncher;
        boolean isHoldingMainHand = player.getHeldItemMainhand().getItem() instanceof ItemJackOLauncher;
        if ((isHoldingMainHand && Minecraft.getInstance().gameSettings.mainHand == EnumHandSide.RIGHT) || (isHoldingOffHand && Minecraft.getInstance().gameSettings.mainHand == EnumHandSide.LEFT)) {
            ((ModelBiped) event.getRenderer().getMainModel()).rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
        } else if ((isHoldingMainHand && Minecraft.getInstance().gameSettings.mainHand == EnumHandSide.LEFT) || (isHoldingOffHand && Minecraft.getInstance().gameSettings.mainHand == EnumHandSide.RIGHT)) {
            ((ModelBiped) event.getRenderer().getMainModel()).leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
        }
    }
}
