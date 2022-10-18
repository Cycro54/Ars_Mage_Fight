package invoker54.magefight.event;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.config.MageFightConfig;
import invoker54.magefight.network.NetworkHandler;
import invoker54.magefight.network.message.SyncConfigMsg;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import static invoker54.magefight.config.MageFightConfig.bakeCommonConfig;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
public class SyncConfigEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        //Make sure you have all the right values
        bakeCommonConfig();

        //Now send the player all of the config values
        NetworkHandler.sendToPlayer(event.getPlayer(), new SyncConfigMsg(MageFightConfig.serialize()));
    }

    @SubscribeEvent
    public static void onUpdateConfig(TickEvent.ServerTickEvent event){
        if (event.type == TickEvent.Type.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (MageFightConfig.isDirty()){
            //Then finally send the config data to all players
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncConfigMsg(MageFightConfig.serialize()));

            MageFightConfig.markDirty(false);
        }
    }
}