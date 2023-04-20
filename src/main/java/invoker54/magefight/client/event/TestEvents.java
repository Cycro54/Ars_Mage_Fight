package invoker54.magefight.client.event;

import invoker54.magefight.ArsMageFight;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, value = Dist.CLIENT)
public class TestEvents {

//    @SubscribeEvent
//    public static void onPause(TickEvent.PlayerTickEvent event){
//        if (event.side == LogicalSide.SERVER) return;
//        if (event.phase == TickEvent.Phase.START) return;
//
//        if (ClientUtil.mC.screen instanceof InventoryScreen){
//            ClientUtil.mC.setScreen(new GlyphStorageScreen());
//        }
//    }
}
