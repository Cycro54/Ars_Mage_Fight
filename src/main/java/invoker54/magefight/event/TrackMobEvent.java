package invoker54.magefight.event;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.potion.FatalBondPotionEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
public class TrackMobEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onTrack(PlayerEvent.StartTracking event){
        if (!(event.getTarget() instanceof LivingEntity)) return;

        LivingEntity trackedEntity = (LivingEntity) event.getTarget();
        if (trackedEntity.level.isClientSide) return;
        if (!trackedEntity.hasEffect(EffectInit.FATAL_BOND_EFFECT)) return;

        //update the mobID while syncing to client
        LivingEntity slaveEntity = FatalBondPotionEffect.getSlave(trackedEntity);
        if (slaveEntity != null) FatalBondPotionEffect.setSlave(trackedEntity, slaveEntity);
    }

//    @SubscribeEvent
//    public static void onJoin(EntityJoinWorldEvent event){
//        if (!(event.getEntity() instanceof LivingEntity)) return;
////        if (!(event.getEntity() instanceof HuskEntity)) return;
//
//        LivingEntity trackedEntity = (LivingEntity) event.getEntity();
//        LOGGER.debug("I AM STARTING TO TRACK AN ENTITY WITH THIS ID: " + event.getEntity().getId());
//        LOGGER.debug("THERE NAME IS: " + trackedEntity.getName().getString());
//        if (trackedEntity.level.isClientSide) return;
//
//        //update the mobID in the
//
//    }
}
