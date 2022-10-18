package invoker54.magefight.network.message;

import invoker54.magefight.capability.player.MagicDataCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SyncRequestMsg {
    private static final Logger LOGGER = LogManager.getLogger();

    public ResourceLocation level;
    public int mobID;

    public SyncRequestMsg(ResourceLocation level, int mobID){
        this.level = level;
        this.mobID = mobID;
    }

    public static void encode(SyncRequestMsg msg, PacketBuffer buffer){
        buffer.writeResourceLocation(msg.level);
        buffer.writeInt(msg.mobID);
    }

    public static SyncRequestMsg decode(PacketBuffer buffer){
        return new SyncRequestMsg(buffer.readResourceLocation(), buffer.readInt());
    }

    //This is how the Network Handler will handle the message
    public static void handle(SyncRequestMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            Iterable<ServerWorld> worlds = server.getAllLevels();
            for(World world : worlds){
                if (world.dimension().getRegistryName().equals(msg.level)){
                    Entity targetEntity = world.getEntity(msg.mobID);
                    if (targetEntity == null){
                        LOGGER.debug("WHAT IN THE WORLD, IT FAILED!! (THE MAGIC DATA REQUEST), Here's the ID: "
                                + world.dimension().getRegistryName() + " , And here's the mob id: " + msg.mobID);
                        break;
                    }
//                    MagicDataCap.refreshCap(targetEntity);
                    MagicDataCap.syncToClient((LivingEntity) targetEntity);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
