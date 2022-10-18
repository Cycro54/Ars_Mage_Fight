package invoker54.magefight.network.message;

import invoker54.magefight.config.MageFightConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncConfigMsg {

    public CompoundNBT configData;

    public SyncConfigMsg(CompoundNBT configData){
        this.configData = configData;
    }

    public static void encode(SyncConfigMsg msg, PacketBuffer buffer){
        buffer.writeNbt(msg.configData);
    }

    public static SyncConfigMsg decode(PacketBuffer buffer) {
        return new SyncConfigMsg(
                buffer.readNbt()
        );
    }

    //This is how the Network Handler will handle the message
    public static void handle(SyncConfigMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            //Make sure only the server sends this
            if (context.getSender() != null) return;

            //Now start to sync the config data
            MageFightConfig.deserialize(msg.configData);
        });
        context.setPacketHandled(true);
    }
}
