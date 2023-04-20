package invoker54.magefight.network.message;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.config.MageFightConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class SellGlyphMsg {
    private static final Logger LOGGER = LogManager.getLogger();

    public String glyphTag;

    public SellGlyphMsg(String glyphTag){
        this.glyphTag = glyphTag;
    }

    public static void encode(SellGlyphMsg msg, PacketBuffer buffer){
        buffer.writeUtf(msg.glyphTag);
    }

    public static SellGlyphMsg decode(PacketBuffer buffer) {
        return new SellGlyphMsg(
                buffer.readUtf()
        );
    }

    //This is how the Network Handler will handle the message
    public static void handle(SellGlyphMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            //Make sure only the client
            if (context.getSender() == null) return;

            ServerPlayerEntity player = context.getSender();
            MagicDataCap cap = MagicDataCap.getCap(player);
            //Make sure the glyph exists first
            if (!ArsNouveauAPI.getInstance().getSpell_map().containsKey(msg.glyphTag)){
                // LOGGER.warn("THIS GLYPH DOESNT EXIST, CAN'T BUY IT!");
                return;
            }

            //Give them half the xp back
            player.giveExperiencePoints((MageFightConfig.baseGlyphPrice + MageFightConfig.pricePerTier)/2);

            //Then remove the glyph for them
            cap.removeSpell(ArsNouveauAPI.getInstance().getSpell_map().get(msg.glyphTag));

            //Make sure to sync their data right after
            MagicDataCap.syncToClient(player);
        });
        context.setPacketHandled(true);
    }
}
