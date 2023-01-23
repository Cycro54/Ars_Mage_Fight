package invoker54.magefight.capability.player;


import invoker54.magefight.ArsMageFight;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
public class CapSaveDelayEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    public static HashMap<LivingEntity, MagicDataCap> delayedCaps = new HashMap<>();

    public static MagicDataCap grabTempCap(LivingEntity entity){
        LOGGER.debug("Grabbing a temporary cap");
        if (!delayedCaps.containsKey(entity)){
            delayedCaps.put(entity, new MagicDataCap());
        }
        return delayedCaps.get(entity);
    }

    //This will be used to save delayed cap data
    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event){
        if (event.phase == TickEvent.Phase.END) return;

        Iterator<LivingEntity> iterator = delayedCaps.keySet().iterator();

        while (iterator.hasNext()){
            LivingEntity entity = iterator.next();
            if (!entity.isAlive()){
                iterator.remove();
                continue;
            }
            MagicDataCap cap = entity.getCapability(MagicDataProvider.CAP_MAGIC_DATA).orElse(null);

            if (cap == null) continue;
            //If it isn't null, make it so the cap data merges
            cap.deserializeNBT(cap.serializeNBT().merge(delayedCaps.get(entity).serializeNBT()));
            LOGGER.debug("I SYNCED THE CAP DATA, REMOVING THIS TEMP CAP NOW");
            LOGGER.debug("OLD SIZE: " + delayedCaps.keySet().size());
            iterator.remove();
            LOGGER.debug("NEW SIZE: " + delayedCaps.keySet().size());
        }
    }
}
