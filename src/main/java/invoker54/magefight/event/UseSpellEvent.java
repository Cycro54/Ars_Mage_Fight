package invoker54.magefight.event;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.capability.Mana;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.config.MageFightConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
public class UseSpellEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void useSpell(SpellResolveEvent.Pre event){
        if (!(event.shooter instanceof PlayerEntity)) return;
        if (((PlayerEntity) event.shooter).isCreative()) return;
        MagicDataCap cap = MagicDataCap.getCap(event.shooter);

        //Get the pool list
        List<AbstractSpellPart> pool_list = MageFightConfig.getPoolList();

        //Get the spell parts list
        Set<AbstractSpellPart> set = new LinkedHashSet<>(event.spell.recipe);
        List<AbstractSpellPart> spell_List = new ArrayList<>(set);
        //Remove all spell parts that aren't in the glyph pool
        spell_List.removeIf((spellPart -> !pool_list.contains(spellPart)));
        //Then remove all spell parts the players has.
        spell_List.removeIf((spellPart -> cap.getUnlockedSpells().contains(spellPart)));

        //If the list ends up being empty, don't do anything
        if (spell_List.isEmpty()) return;

        String spellNames = "";
        for (AbstractSpellPart spellPart: spell_List){
            spellNames = spellNames.concat(spellPart.getLocaleName() + ", ");
        }
        spellNames = spellNames.substring(0, spellNames.length() - 2);

        //Make sure the player knows what glyphs they do not have
        PortUtil.sendMessageNoSpam(event.shooter,
                new TranslationTextComponent("ars_mage_fight.chat.missing_spell_part1")
                .append(spellNames)
                .append(new TranslationTextComponent("ars_mage_fight.chat.missing_spell_part2")));

        event.setCanceled(true);
    }
}
