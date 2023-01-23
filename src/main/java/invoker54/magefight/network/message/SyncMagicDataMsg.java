package invoker54.magefight.network.message;

import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SyncMagicDataMsg {
    private static final Logger LOGGER = LogManager.getLogger();

    public CompoundNBT magicData;
    public int mobID;

    public SyncMagicDataMsg(CompoundNBT magicData, int mobID){
        this.magicData = magicData;
        this.mobID = mobID;
    }

    public static void encode(SyncMagicDataMsg msg, PacketBuffer buffer){
        buffer.writeNbt(msg.magicData);
        buffer.writeInt(msg.mobID);
    }

    public static SyncMagicDataMsg decode(PacketBuffer buffer){
        return new SyncMagicDataMsg(buffer.readNbt(), buffer.readInt());
    }

    //This is how the Network Handler will handle the message
    public static void handle(SyncMagicDataMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            LOGGER.debug("THIS IS THE MOB ID I AM SYNCING CAP DATA FOR: " + msg.mobID);
            LivingEntity mobEntity = (LivingEntity) Minecraft.getInstance().level.getEntity(msg.mobID);
            if (mobEntity == null){
                LOGGER.fatal("MOB ENTITY IS MISSING, THIS WAS CAUGHT IN SyncMagicDataMsg!!");
                return;
            }
//            MagicDataCap.refreshCap(mobEntity);
            LOGGER.debug("HERES THERE NAME: " + mobEntity.getName().getString());
            MagicDataCap.getCap(mobEntity).deserializeNBT(msg.magicData);
        });
        context.setPacketHandled(true);
    }
}
