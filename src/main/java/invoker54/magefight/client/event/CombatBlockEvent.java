package invoker54.magefight.client.event;


import invoker54.magefight.ArsMageFight;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.client.screen.GlyphStorageScreen;
import invoker54.magefight.init.BlockInit;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, value = Dist.CLIENT)
public class CombatBlockEvent {
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickBlock event){
        if (!event.getWorld().getBlockState(event.getPos()).is(BlockInit.COMBAT_BLOCK)) return;
        event.setResult(Event.Result.DENY);
        event.setCanceled(true);
        if (!event.getWorld().isClientSide) return;
        if (ClientUtil.mC.screen != null) return;

        ClientUtil.mC.setScreen(new GlyphStorageScreen());
        LOGGER.debug("GOING TO GLYPH STORAGE");
        ClientUtil.mC.player.playSound(SoundEvents.SOUL_SOIL_HIT, 0.75F, 0.5F);
    }
}
