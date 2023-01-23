package invoker54.magefight.network.message;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.config.MageFightConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class BuyGlyphMsg {
    private static final Logger LOGGER = LogManager.getLogger();

    public String glyphTag;

    public BuyGlyphMsg(String glyphTag){
        this.glyphTag = glyphTag;
    }

    public static void encode(BuyGlyphMsg msg, PacketBuffer buffer){
        buffer.writeUtf(msg.glyphTag);
    }

    public static BuyGlyphMsg decode(PacketBuffer buffer) {
        return new BuyGlyphMsg(
                buffer.readUtf()
        );
    }

    //This is how the Network Handler will handle the message
    public static void handle(BuyGlyphMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            //Make sure only the client
            if (context.getSender() == null) return;

            ServerPlayerEntity player = context.getSender();
            MagicDataCap cap = MagicDataCap.getCap(player);
            //Make sure the glyph exists first
            if (!ArsNouveauAPI.getInstance().getSpell_map().containsKey(msg.glyphTag)){
                LOGGER.warn("THIS GLYPH DOESNT EXIST, CAN'T BUY IT!");
                return;
            }

            int cost = 0;
            //add up all the glyphs
            for (AbstractSpellPart spellPart : cap.getUnlockedSpells()){
                cost += ((spellPart.getTier().ordinal() + 1) * MageFightConfig.pricePerTier);
            }
            //Then add the base price at the end
            cost += MageFightConfig.baseGlyphPrice;

            //Take the players xp
            player.giveExperiencePoints(-(cost));

            //Then unlock the glyph for them
            cap.addSpell(ArsNouveauAPI.getInstance().getSpell_map().get(msg.glyphTag));
            if (MageFightConfig.autoUnlockGlyph){
                ItemStack stack = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(msg.glyphTag));
                LOGGER.debug("ITEM I AM ABOUT TO ADD: " + stack.getDisplayName().getString());
                player.addItem(stack);
            }

            //Remove the temp spells
            cap.removeTempSpells();

            //Make sure to sync their data right after
            MagicDataCap.syncToClient(player);
        });
        context.setPacketHandled(true);
    }
}
