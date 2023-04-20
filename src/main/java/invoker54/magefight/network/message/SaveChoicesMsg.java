package invoker54.magefight.network.message;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import invoker54.magefight.capability.player.MagicDataCap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class SaveChoicesMsg {
    private static final Logger LOGGER = LogManager.getLogger();

    public String chosenSpells;

    public SaveChoicesMsg(String chosenSpells){
        this.chosenSpells = chosenSpells;
    }

    public static void encode(SaveChoicesMsg msg, PacketBuffer buffer){
        buffer.writeUtf(msg.chosenSpells);
    }

    public static SaveChoicesMsg decode(PacketBuffer buffer) {
        return new SaveChoicesMsg(
                buffer.readUtf()
        );
    }

    //This is how the Network Handler will handle the message
    public static void handle(SaveChoicesMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            //Make sure only the client
            if (context.getSender() == null) return;

            ServerPlayerEntity player = context.getSender();
            MagicDataCap cap = MagicDataCap.getCap(player);

            String[] stringSpells = msg.chosenSpells.split(",");
            ArrayList<AbstractSpellPart> chosenSpells = new ArrayList<>();
            if (stringSpells.length != 0 && !Objects.equals(stringSpells[0], "")) {
                Map<String, AbstractSpellPart> spellMap = ArsNouveauAPI.getInstance().getSpell_map();
                for (String spellPart : stringSpells){
                    if (spellMap.containsKey(spellPart)){
                        chosenSpells.add(spellMap.get(spellPart));
                    }
                }
            }

            cap.saveTempSpells(chosenSpells);

            //Make sure to sync their data right after
            MagicDataCap.syncToClient(player);
        });
        context.setPacketHandled(true);
    }
}
