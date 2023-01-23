package invoker54.magefight.network;

import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.network.message.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    //Increment the first number if you add new stuff to NetworkHandler class
    //Increment the middle number each time you make a new Message
    //Increment the last number each time you fix a bug
    private static final String PROTOCOL_VERSION = "1.5.0";

    private static int ID = 0;
    public static int nextID(){return ID++;}

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(

            //Name of the channel
            new ResourceLocation(ArsMageFight.MOD_ID, "network"),
            //Supplier<String> that returns protocol version
            () -> PROTOCOL_VERSION,
            //Checks incoming network protocol version for client (so it's pretty much PROTOCOL_VERSION == INCOMING_PROTOCOL_VERSION)
            PROTOCOL_VERSION::equals,
            //Checks incoming network protocol version for server (If they don't equal, it won't work.)
            PROTOCOL_VERSION::equals
    );

    public static void init(){
        // This is how you avoid sending anything to the server when you don't need to.
        // (change encode with an empty lambda, and just make decode create a new instance of the target message class)
        // INSTANCE.registerMessage(0, SpawnDiamondMsg.class, (message, buf) -> {}, it -> new SpawnDiamondMsg(), SpawnDiamondMsg::handle);
        // INSTANCE.registerMessage(0, SyncClientCapMsg.class, SyncClientCapMsg::Encode, SyncClientCapMsg::Decode, SyncClientCapMsg::handle);
         INSTANCE.registerMessage(nextID(), SyncMagicDataMsg.class, SyncMagicDataMsg::encode, SyncMagicDataMsg::decode, SyncMagicDataMsg::handle);
         INSTANCE.registerMessage(nextID(), SyncRequestMsg.class, SyncRequestMsg::encode, SyncRequestMsg::decode, SyncRequestMsg::handle);
         INSTANCE.registerMessage(nextID(), SyncConfigMsg.class, SyncConfigMsg::encode, SyncConfigMsg::decode, SyncConfigMsg::handle);
         INSTANCE.registerMessage(nextID(), BuyGlyphMsg.class, BuyGlyphMsg::encode, BuyGlyphMsg::decode, BuyGlyphMsg::handle);
         INSTANCE.registerMessage(nextID(), SellGlyphMsg.class, SellGlyphMsg::encode, SellGlyphMsg::decode, SellGlyphMsg::handle);
         INSTANCE.registerMessage(nextID(), SaveChoicesMsg.class, SaveChoicesMsg::encode, SaveChoicesMsg::decode, SaveChoicesMsg::handle);
    }


    //Custom method used to send data to players
    public static void sendToPlayer(PlayerEntity player, Object message) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }
}
