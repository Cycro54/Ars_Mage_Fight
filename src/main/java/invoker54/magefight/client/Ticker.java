package invoker54.magefight.client;

import invoker54.magefight.ArsMageFight;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsMageFight.MOD_ID)
public class Ticker {
    private static long ticksInGame = 0;
    private static float partialTicks = 0;
    private static float delta = 0;
    private static float total = 0;
    private static final Logger LOGGER = LogManager.getLogger();

    private static void calcDelta() {
        float oldTotal = total;
        total = ticksInGame + partialTicks;
        delta = total - oldTotal;
    }

    @SubscribeEvent
    protected static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            partialTicks = event.renderTickTime;
        } else {
            calcDelta();
        }
    }

    @SubscribeEvent
    protected static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ticksInGame++;
            partialTicks = 0;

            calcDelta();
        }
    }

    //returns how much actual time has passed (in ticks)
    public static float getDelta(boolean canPause, boolean inTicks){
        return (canPause ? (ClientUtil.mC.isPaused() ? 0 : delta) : delta)/(inTicks ? 1 : 20);
    }
}
