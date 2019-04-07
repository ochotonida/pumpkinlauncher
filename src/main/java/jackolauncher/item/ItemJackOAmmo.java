package jackolauncher.item;

import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemJackOAmmo extends Item {

    public ItemJackOAmmo() {
        super(new Properties().group(ItemGroup.COMBAT));
        setRegistryName(JackOLauncher.MODID, "jack_o_ammo");
    }


}
