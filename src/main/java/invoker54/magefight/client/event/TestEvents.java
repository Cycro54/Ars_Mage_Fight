package invoker54.magefight.client.event;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.client.screen.GlyphStorageScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
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
