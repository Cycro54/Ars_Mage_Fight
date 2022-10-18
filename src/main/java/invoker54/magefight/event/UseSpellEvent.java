package invoker54.magefight.event;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.config.MageFightConfig;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
public class UseSpellEvent {

    @SubscribeEvent
    public static void useSpell(SpellResolveEvent.Pre event){
        if (!(event.shooter instanceof PlayerEntity)) return;
        if (((PlayerEntity) event.shooter).isCreative()) return;
        MagicDataCap cap = MagicDataCap.getCap(event.shooter);

        //Get the pool list
        List<AbstractSpellPart> pool_list = new ArrayList<>();
        Map<String, AbstractSpellPart> spellPartMap = ArsNouveauAPI.getInstance().getSpell_map();
        for (String spellTag: MageFightConfig.randomGlyphPool){
            if (!spellPartMap.containsKey(spellTag)) continue;

            pool_list.add(spellPartMap.get(spellTag));
        }

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
