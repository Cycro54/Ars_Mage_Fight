package invoker54.magefight.init;

import invoker54.magefight.capability.player.MagicDataCap;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapInit {
    public static void registerCaps(){
        CapabilityManager.INSTANCE.register(MagicDataCap.class, new MagicDataCap.MagicDataStorage(), MagicDataCap::new);
    }
}
